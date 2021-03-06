package io.github.mincongh.entity;

import java.io.Serializable;

import javax.persistence.*;

/**
 * The persistent class for the address database table.
 * 
 * @author Mincong HUANG
 */
@Entity
@NamedQuery(name="Address.findAll", query="SELECT a FROM Address a")
@Table(uniqueConstraints={@UniqueConstraint(columnNames={"id", "seq"})})
public class Address implements Serializable {
    
	private static final long serialVersionUID = 1L;

	@Id
    @Column(name="address_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int addressId;
	
	@Column(columnDefinition="char(10)")
	private String id;
	
	@Column(columnDefinition="char(3)")
	private String seq;
	
	private float endlat;

	private float endlong;

	@Column(columnDefinition="char(11)")
	private String leftaddr1;

	@Column(columnDefinition="char(11)")
	private String leftaddr2;

	private int leftzip;

	@Column(columnDefinition="char(30)")
	private String name;

	@Column(name="name_dtmf", columnDefinition="char(30)")
	private String nameDtmf;

	@Column(columnDefinition="char(2)")
	private String prefix;

	@Column(name="prefix_dtmf", columnDefinition="char(2)")
	private String prefixDtmf;

	@Column(columnDefinition="char(11)")
	private String rightaddr1;

	@Column(columnDefinition="char(11)")
	private String rightaddr2;

	private int rightzip;

	private float startlat;

	private float startlong;

	@Column(columnDefinition="char(4)")
	private String type;

	@Column(name="type_dtmf", columnDefinition="char(4)")
	private String typeDtmf;

	public Address() {
	}

	public int getAddressId() {
	    return this.addressId;
	}
	
	public void setAddressId(int addressId) {
	    this.addressId = addressId;
	}
	
	public float getEndlat() {
		return this.endlat;
	}

	public void setEndlat(float endlat) {
		this.endlat = endlat;
	}

	public float getEndlong() {
		return this.endlong;
	}

	public void setEndlong(float endlong) {
		this.endlong = endlong;
	}
	
	public String getId() {
	    return this.id;
	}
	
	public void setId(String id) {
	    this.id = id;
	}

	public String getLeftaddr1() {
		return this.leftaddr1;
	}

	public void setLeftaddr1(String leftaddr1) {
		this.leftaddr1 = leftaddr1;
	}

	public String getLeftaddr2() {
		return this.leftaddr2;
	}

	public void setLeftaddr2(String leftaddr2) {
		this.leftaddr2 = leftaddr2;
	}

	public int getLeftzip() {
		return this.leftzip;
	}

	public void setLeftzip(int leftzip) {
		this.leftzip = leftzip;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameDtmf() {
		return this.nameDtmf;
	}

	public void setNameDtmf(String nameDtmf) {
		this.nameDtmf = nameDtmf;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefixDtmf() {
		return this.prefixDtmf;
	}

	public void setPrefixDtmf(String prefixDtmf) {
		this.prefixDtmf = prefixDtmf;
	}

	public String getRightaddr1() {
		return this.rightaddr1;
	}

	public void setRightaddr1(String rightaddr1) {
		this.rightaddr1 = rightaddr1;
	}

	public String getRightaddr2() {
		return this.rightaddr2;
	}

	public void setRightaddr2(String rightaddr2) {
		this.rightaddr2 = rightaddr2;
	}

	public int getRightzip() {
		return this.rightzip;
	}

	public void setRightzip(int rightzip) {
		this.rightzip = rightzip;
	}
	
	public void setSeq(String seq) {
	    this.seq = seq;
	}

	public String getSeq() {
	    return this.seq;
	}
	
	public float getStartlat() {
		return this.startlat;
	}

	public void setStartlat(float startlat) {
		this.startlat = startlat;
	}

	public float getStartlong() {
		return this.startlong;
	}

	public void setStartlong(float startlong) {
		this.startlong = startlong;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTypeDtmf() {
		return this.typeDtmf;
	}

	public void setTypeDtmf(String typeDtmf) {
		this.typeDtmf = typeDtmf;
	}

    @Override
    public String toString() {
        return "Address [addressId=" + addressId + ", id=" + id + ", seq=" + seq
                + ", endlat=" + endlat + ", endlong=" + endlong + ", leftaddr1="
                + leftaddr1 + ", leftaddr2=" + leftaddr2 + ", leftzip="
                + leftzip + ", name=" + name + ", nameDtmf=" + nameDtmf
                + ", prefix=" + prefix + ", prefixDtmf=" + prefixDtmf
                + ", rightaddr1=" + rightaddr1 + ", rightaddr2=" + rightaddr2
                + ", rightzip=" + rightzip + ", startlat=" + startlat
                + ", startlong=" + startlong + ", type=" + type + ", typeDtmf="
                + typeDtmf + "]";
    }
}
