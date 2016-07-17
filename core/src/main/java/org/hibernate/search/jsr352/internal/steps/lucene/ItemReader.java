/*
 * Hibernate Search, full-text search for your domain model
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.search.jsr352.internal.steps.lucene;

import java.io.Serializable;

import javax.batch.api.BatchProperty;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;

import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.hcore.util.impl.ContextHelper;
import org.hibernate.search.jsr352.internal.JobContextData;
import org.jboss.logging.Logger;

/**
 * TODO: update description. Read entity IDs from {@code IndexingContext}. Each
 * time, there's one array being read. The number of IDs inside the array
 * depends on the array capacity. This value is defined before the job start.
 * Either the default value defined in the job xml will be applied, or the value
 * overwritten by the user in job parameters. These IDs will be processed in
 * {@code BatchItemProcessor}, then be used for Lucene document production.
 * <p>
 * The motivation of using an array of IDs over a single ID is to accelerate the
 * entity processing. Use a SELECT statement to obtain only one ID is rather a
 * waste. For more detail about the entity process, please check {@code
 * BatchItemProcessor}.
 *
 * @author Mincong Huang
 */
@Named
public class ItemReader implements javax.batch.api.chunk.ItemReader {

	private static final Logger logger = Logger.getLogger( ItemReader.class );

	@Inject
	@BatchProperty
	private int maxResults;

	// The offset at the beginning of the entity scroll, starting from 0, e.g.
	// if scroll-offset is 0, then reader starts reading at entity 1.
	// if scroll-offset is 1, then reader starts reading at entity 2.
	@Inject
	@BatchProperty
	private int scrollOffset;

	// The interval of for the next scroll, e.g. if scrollInverval is 3, then
	// reader will read entity [ 1, 4, 7, ... ]
	@Inject
	@BatchProperty
	private int scrollInterval;

	@Inject
	@BatchProperty
	private String entityName;

	@Inject
	@BatchProperty
	private String persistenceUnitName;

	private Class<?> entityClazz;
	private Serializable checkpointId;
	private final JobContext jobContext;

	// read entities and produce Lucene work
	private EntityManager em;
	private Session session;
	private StatelessSession ss;
	private ScrollableResults scroll;
	private boolean hasMoreItem = true;

	@Inject
	public ItemReader(JobContext jobContext) {
		this.jobContext = jobContext;
	}

	/**
	 * The checkpointInfo method returns the current checkpoint data for this
	 * reader. It is called before a chunk checkpoint is committed.
	 *
	 * @return the checkpoint info
	 * @throws Exception thrown for any errors.
	 */
	@Override
	public Serializable checkpointInfo() throws Exception {
		logger.info( "checkpointInfo() called. "
				+ "Saving last read ID to batch runtime..." );
		return checkpointId;
	}

	/**
	 * Close operation(s) before the class destruction.
	 *
	 * @throws Exception thrown for any errors.
	 */
	@Override
	public void close() throws Exception {
		logger.info( "closing everything..." );
		try {
			scroll.close();
			logger.info( "Scrollable results closed." );
		}
		catch (Exception e) {
			logger.error( e );
		}
		try {
			ss.close();
			logger.info( "Stateless session closed." );
		}
		catch (Exception e) {
			logger.error( e );
		}
		try {
			session.close();
			logger.info( "Session closed" );
		}
		catch (Exception e) {
			logger.error( e );
		}
	}

	/**
	 * Initialize the environment. If checkpoint does not exist, then it should
	 * be the first open. If checkpoint exists, then it isn't the first open,
	 * re-use the input object "checkpoint" as the last ID already read.
	 *
	 * @param checkpoint The last checkpoint info persisted in the batch
	 * runtime, previously given by checkpointInfo(). If this is the first
	 * start, then the checkpoint will be null.
	 * @throws Exception thrown for any errors.
	 */
	@Override
	public void open(Serializable checkpoint) throws Exception {

		logger.infof( "open reader for entity %s ...", entityName );
		entityClazz = ( (JobContextData) jobContext.getTransientUserData() )
				.getIndexedType( entityName );
		String path = "java:comp/env/" + persistenceUnitName;
		em = (EntityManager) InitialContext.doLookup( path );
		session = em.unwrap( Session.class );

		ss = session.getSessionFactory().openStatelessSession();
		String idName = ContextHelper
				.getSearchintegrator( session )
				.getIndexBindings()
				.get( entityClazz )
				.getDocumentBuilder()
				.getIdentifierName();

		if ( checkpoint == null ) {
			scroll = ss.createCriteria( entityClazz )
					.addOrder( Order.asc( idName ) )
					.setReadOnly( true )
					.setCacheable( true )
					.setFetchSize( 1000 )
					.setMaxResults( maxResults )
					.scroll( ScrollMode.FORWARD_ONLY );
			hasMoreItem = scroll.scroll( 1 + scrollOffset );
		}
		else {
			checkpointId = checkpoint;
			scroll = ss.createCriteria( entityClazz )
					.add( Restrictions.ge( idName, checkpointId ) )
					.addOrder( Order.asc( idName ) )
					.setReadOnly( true )
					.setCacheable( true )
					.setFetchSize( 1000 )
					.setMaxResults( maxResults )
					.scroll( ScrollMode.FORWARD_ONLY );
			hasMoreItem = scroll.scroll( scrollInterval );
		}
	}

	/**
	 * Read item from database using JPA. Each read, there will be only one
	 * entity fetched.
	 *
	 * @throws Exception thrown for any errors.
	 */
	@Override
	public Object readItem() throws Exception {
		logger.debug( "Reading item ..." );
		Object entity = null;

		if ( hasMoreItem ) {
			entity = scroll.get( 0 );
			checkpointId = (Serializable) em.getEntityManagerFactory()
					.getPersistenceUnitUtil()
					.getIdentifier( entity );
			hasMoreItem = scroll.scroll( scrollInterval );
		}
		else {
			logger.info( "no more result. read ends." );
		}
		return entity;
	}
}
