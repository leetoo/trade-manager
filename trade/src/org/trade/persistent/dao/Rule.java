/* ===========================================================
 * TradeManager : a application to trade strategies for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Project Info:  org.trade
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Java is a trademark or registered trademark of Oracle, Inc.
 * in the United States and other countries.]
 *
 * (C) Copyright 2011-2011, by Simon Allen and Contributors.
 *
 * Original Author:  Simon Allen;
 * Contributor(s):   -;
 *
 * Changes
 * -------
 *
 */
package org.trade.persistent.dao;

// Generated Feb 21, 2011 12:43:33 PM by Hibernate Tools 3.4.0.CR1

import static javax.persistence.GenerationType.IDENTITY;

import java.util.Comparator;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.trade.core.dao.Aspect;
import org.trade.core.util.CoreUtils;

/**
 * Rule generated by hbm2java
 * @author Simon Allen
 * @version $Revision: 1.0 $
 */
@Entity
@Table(name = "rule")
public class Rule extends Aspect implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2273276207080568947L;
	private Strategy strategy;
	private String comment;
	private Date createDate;
	private Date updateDate;
	private byte[] rule;
	private boolean dirty = false;

	public Rule() {
	}

	/**
	 * Constructor for Rule.
	 * @param strategy Strategy
	 * @param version Integer
	 * @param comment String
	 * @param createDate Date
	 * @param updateDate Date
	 */
	public Rule(Strategy strategy, Integer version, String comment,
			Date createDate, Date updateDate) {
		this.strategy = strategy;
		this.version = version;
		this.comment = comment;
		this.createDate = createDate;
		this.updateDate = updateDate;
	}

	/**
	 * Constructor for Rule.
	 * @param strategy Strategy
	 * @param version Integer
	 * @param comment String
	 * @param createDate Date
	 * @param rule byte[]
	 * @param updateDate Date
	 */
	public Rule(Strategy strategy, Integer version, String comment,
			Date createDate, byte[] rule, Date updateDate) {
		this.strategy = strategy;
		this.version = version;
		this.comment = comment;
		this.createDate = createDate;
		this.rule = rule;
		this.updateDate = updateDate;
	}

	/**
	 * Method getIdRule.
	 * @return Integer
	 */
	@Id
	@GeneratedValue(strategy = IDENTITY)
	@Column(name = "idRule", unique = true, nullable = false)
	public Integer getIdRule() {
		return this.id;
	}

	/**
	 * Method setIdRule.
	 * @param idRule Integer
	 */
	public void setIdRule(Integer idRule) {
		this.id = idRule;
	}

	/**
	 * Method getStrategy.
	 * @return Strategy
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idStrategy", nullable = false)
	public Strategy getStrategy() {
		return this.strategy;
	}

	/**
	 * Method setStrategy.
	 * @param strategy Strategy
	 */
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}

	/**
	 * Method getComment.
	 * @return String
	 */
	@Column(name = "comment", nullable = false, length = 200)
	public String getComment() {
		return this.comment;
	}

	/**
	 * Method setComment.
	 * @param comment String
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Method getCreateDate.
	 * @return Date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "createDate", nullable = false, length = 19)
	public Date getCreateDate() {
		return this.createDate;
	}

	/**
	 * Method setCreateDate.
	 * @param createDate Date
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	/**
	 * Method getUpdateDate.
	 * @return Date
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updateDate", nullable = false, length = 19)
	public Date getUpdateDate() {
		return this.updateDate;
	}

	/**
	 * Method setUpdateDate.
	 * @param updateDate Date
	 */
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	/**
	 * Method getRule.
	 * @return byte[]
	 */
	@Lob
	@Column(name = "rule")
	public byte[] getRule() {
		return this.rule;
	}

	/**
	 * Method setRule.
	 * @param rule byte[]
	 */
	public void setRule(byte[] rule) {
		this.rule = rule;
	}

	/**
	 * Method getVersion.
	 * @return Integer
	 */
	@Column(name = "version", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	/**
	 * Method setVersion.
	 * @param version Integer
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * Method isDirty.
	 * @return boolean
	 */
	@Transient
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Method setDirty.
	 * @param dirty boolean
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	/**
	 * Method toString.
	 * @return String
	 */
	public String toString() {
		return "Version-" + this.getVersion();
	}

	public static final Comparator<Rule> VERSION_ORDER = new Comparator<Rule>() {
		public int compare(Rule o1, Rule o2) {
			return CoreUtils.nullSafeIntegerComparator(o1.getVersion(),
					o2.getVersion());
		}
	};

	/**
	 * Method equals.
	 * @param objectToCompare Object
	 * @return boolean
	 */
	public boolean equals(Object objectToCompare) {

		if (super.equals(objectToCompare))
			return true;

		if (objectToCompare instanceof Rule) {
			if (null == this.getIdRule() || null == this.getVersion()) {
				return false;
			}

			if (this.getStrategy()
					.getIdStrategy()
					.equals(((Rule) objectToCompare).getStrategy()
							.getIdStrategy())
					&& this.getVersion().equals(
							((Rule) objectToCompare).getVersion())) {
				return true;
			}

		}
		return false;
	}
}
