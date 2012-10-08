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
package org.trade.ui.models;

import java.util.Vector;

import javax.swing.event.TableModelEvent;

import org.trade.core.valuetype.Date;
import org.trade.core.valuetype.Money;
import org.trade.core.valuetype.Quantity;
import org.trade.core.valuetype.YesNo;
import org.trade.dictionary.valuetype.Action;
import org.trade.dictionary.valuetype.DAOEntryLimit;
import org.trade.dictionary.valuetype.OrderStatus;
import org.trade.dictionary.valuetype.OrderType;
import org.trade.dictionary.valuetype.Side;
import org.trade.persistent.dao.Entrylimit;
import org.trade.persistent.dao.Trade;
import org.trade.persistent.dao.TradeOrder;
import org.trade.persistent.dao.Tradestrategy;
import org.trade.ui.base.TableModel;

/**
 */
public class TradeOrderTableModel extends TableModel {

	private static final long serialVersionUID = 3087514589731145479L;

	private static final String SYMBOL = "Symbol*";
	private static final String ORDER_KEY = " Order Key ";
	private static final String QUANTITY = "Qty*";
	private static final String ACTION = "Action*";
	private static final String ORDER_TYPE = "Type*";
	private static final String LMT_PRICE = "Lmt Price";
	private static final String AUX_PRICE = "Aux Price";
	private static final String TRANSMIT = "Transmit";
	private static final String STATUS = "   Status   ";
	private static final String OCA_GRP_NAME = "OCA Id";
	private static final String AVG_PRICE = "Avg Price";
	private static final String FILLED_DATE = "Trade Time";
	private static final String FILLED_QTY = "Filled Qty";
	private static final String STOP_PRICE = "Stop Price";

	private Tradestrategy m_data = null;

	public TradeOrderTableModel() {
		// Get the column names and cache them.
		// Then we can close the connection.
		columnNames = new String[14];
		columnNames[0] = SYMBOL;
		columnNames[1] = ORDER_KEY;
		columnNames[2] = ACTION;
		columnNames[3] = ORDER_TYPE;
		columnNames[4] = QUANTITY;
		columnNames[5] = LMT_PRICE;
		columnNames[6] = AUX_PRICE;
		columnNames[7] = TRANSMIT;
		columnNames[8] = STATUS;
		columnNames[9] = OCA_GRP_NAME;
		columnNames[10] = AVG_PRICE;
		columnNames[11] = FILLED_DATE;
		columnNames[12] = FILLED_QTY;
		columnNames[13] = STOP_PRICE;

	}

	/**
	 * Method isCellEditable.
	 * @param row int
	 * @param column int
	 * @return boolean
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int row, int column) {
		OrderStatus orderStatus = (OrderStatus) this.getValueAt(row, 8);
		if (null != orderStatus) {
			if (OrderStatus.CANCELLED.equals(orderStatus.getCode())
					|| OrderStatus.FILLED.equals(orderStatus.getCode())) {
				return false;
			}
		}
		if ((column == 0) || (column == 1) || (column == 8) || (column == 10)
				|| (column == 11) || (column == 12) || (column == 13)) {
			return false;
		}
		return true;
	}

	/**
	 * Method getData.
	 * @return Tradestrategy
	 */
	public Tradestrategy getData() {
		return m_data;
	}

	/**
	 * Method setData.
	 * @param data Tradestrategy
	 */
	public void setData(Tradestrategy data) {
		this.m_data = data;
		this.clearAll();
		if (!getData().getTrades().isEmpty()) {
			for (final Trade element : getData().getTrades()) {
				for (final TradeOrder tradeOrder : element.getTradeOrders()) {
					final Vector<Object> newRow = new Vector<Object>();
					getNewRow(newRow, tradeOrder);
					rows.add(newRow);
				}
			}
			fireTableDataChanged();
		}
	}

	/**
	 * Method populateDAO.
	 * @param value Object
	 * @param row int
	 * @param column int
	 */
	public void populateDAO(Object value, int row, int column) {

		TradeOrder element = null;

		int i = 0;
		for (final Trade trade : getData().getTrades()) {
			for (final TradeOrder tradeOrder : trade.getTradeOrders()) {
				if (i == row) {
					element = tradeOrder;
					break;
				}
				i++;
			}
		}

		switch (column) {
		case 0: {

			element.getTrade().getTradestrategy().getContract()
					.setSymbol((String) value);
			break;
		}
		case 1: {
			element.setOrderKey(((Quantity) value).getIntegerValue());
			break;
		}
		case 2: {
			element.setAction(((Action) value).getCode());
			break;
		}
		case 3: {
			element.setOrderType(((OrderType) value).getCode());
			break;
		}
		case 4: {
			element.setQuantity(((Quantity) value).getIntegerValue());
			break;
		}
		case 5: {
			element.setLimitPrice(((Money) value).getBigDecimalValue());
			break;
		}
		case 6: {
			element.setAuxPrice(((Money) value).getBigDecimalValue());
			break;
		}
		case 7: {
			element.setTransmit(new Boolean(((YesNo) value).getCode()));
			break;
		}
		case 8: {
			element.setStatus(((OrderStatus) value).getCode());
			break;
		}
		case 9: {
			element.setOcaGroupName((String) value);
			break;
		}
		case 10: {
			element.setAverageFilledPrice(((Money) value).getBigDecimalValue());
			break;
		}
		case 11: {
			element.setFilledDate(((Date) value).getDate());
			break;
		}
		case 12: {
			element.setFilledQuantity(((Quantity) value).getIntegerValue());
			break;
		}
		case 13: {
			element.setStopPrice(((Money) value).getBigDecimalValue());
			break;
		}
		default: {
		}
		}
	}

	/**
	 * Method deleteRow.
	 * @param selectedRow int
	 */
	public void deleteRow(int selectedRow) {
		final Vector<Object> currRow = rows.get(selectedRow);

		int i = 0;
		for (final Trade trade : getData().getTrades()) {
			for (final TradeOrder element : trade.getTradeOrders()) {
				if (i == selectedRow) {
					trade.getTradeOrders().remove(element);
					rows.remove(currRow);
					fireTableChanged(new TableModelEvent(this));
					break;
				}
				i++;
			}
		}
	}

	/**
	 * Method addRow.
	 * @param element TradeOrder
	 */
	public void addRow(TradeOrder element) {

		element.getTrade().addTradeOrder(element);
		getData().addTrade(element.getTrade());
		final Vector<Object> newRow = new Vector<Object>();
		getNewRow(newRow, element);
		rows.add(newRow);

		// Tell the listeners a new table has arrived.
		fireTableChanged(new TableModelEvent(this));

	}

	public void addRow() {
		final Tradestrategy tradestrategy = getData();

		final String side = tradestrategy.getSide();
		String action = Action.BUY;
		if (Side.SLD.equals(side)) {
			action = Action.SELL;
		}
		final double risk = tradestrategy.getRiskAmount().doubleValue();
		final double stop = 1.0d;
		Money price = new Money(0);
		if (tradestrategy.getDatasetContainer().getCandleDataset()
				.getItemCount(0) > 0) {
			price = new Money(tradestrategy
					.getDatasetContainer()
					.getCandleDataset()
					.getCloseValue(
							0,
							(tradestrategy.getDatasetContainer()
									.getCandleDataset().getItemCount(0) - 1)));
		}
		final int quantlty = (int) ((int) risk / stop);
		final Date createDate = new Date(new java.util.Date());

		Trade trade = null;
		boolean openPosition = false;
		if (null != tradestrategy.getOpenTrade()) {
			trade = tradestrategy.getOpenTrade();
		} else {
			trade = new Trade(tradestrategy, tradestrategy.getSide());
			tradestrategy.getTrades().add(trade);
			openPosition = true;
		}

		int buySellMultiplier = 1;

		if (action.equals(Action.BUY)) {
			action = Action.SELL;

		} else {
			action = Action.BUY;
			buySellMultiplier = -1;
		}
		final Entrylimit entrylimit = DAOEntryLimit.newInstance().getValue(
				price);
		final TradeOrder tradeOrder = new TradeOrder(trade, action,
				OrderType.STPLMT, quantlty, price.getBigDecimalValue(), price
						.add(new Money(entrylimit.getLimitAmount()
								.doubleValue() * buySellMultiplier))
						.getBigDecimalValue(), createDate.getDate());
		tradeOrder.setIsOpenPosition(openPosition);
		tradeOrder.setOcaGroupName("");
		tradeOrder.setStatus(OrderStatus.newInstance().getCode());
		trade.addTradeOrder(tradeOrder);

		final Vector<Object> newRow = new Vector<Object>();
		getNewRow(newRow, tradeOrder);
		rows.add(newRow);

		// Tell the listeners a new table has arrived.

		fireTableChanged(new TableModelEvent(this));
	}

	/**
	 * Method getNewRow.
	 * @param newRow Vector<Object>
	 * @param element TradeOrder
	 */
	public void getNewRow(Vector<Object> newRow, TradeOrder element) {

		newRow.addElement(element.getTrade().getTradestrategy().getContract()
				.getSymbol());
		newRow.addElement(new Quantity(element.getOrderKey()));
		if (null == element.getAction()) {
			newRow.addElement(new Action());
		} else {
			newRow.addElement(Action.newInstance(element.getAction()));
		}
		if (null == element.getOrderType()) {
			newRow.addElement(new OrderType());
		} else {
			newRow.addElement(OrderType.newInstance(element.getOrderType()));
		}

		newRow.addElement(new Quantity(element.getQuantity()));
		newRow.addElement(new Money(element.getLimitPrice()));
		newRow.addElement(new Money(element.getAuxPrice()));
		newRow.addElement(YesNo.newInstance(element.getTransmit()));
		if (null == element.getStatus()) {
			newRow.addElement(new OrderStatus());
		} else {
			newRow.addElement(OrderStatus.newInstance(element.getStatus()));
		}
		if (null == element.getOcaGroupName()) {
			newRow.addElement("");
		} else {
			newRow.addElement(element.getOcaGroupName());
		}
		if (null == element.getAverageFilledPrice()) {
			newRow.addElement(new Money());
		} else {
			newRow.addElement(new Money(element.getAverageFilledPrice()));
		}
		if (null == element.getFilledDate()) {
			newRow.addElement(new Date());
		} else {
			newRow.addElement(new Date(element.getFilledDate()));
		}
		newRow.addElement(new Quantity(element.getFilledQuantity()));
		newRow.addElement(new Money(element.getStopPrice()));
	}
}
