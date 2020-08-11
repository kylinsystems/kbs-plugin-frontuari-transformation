package net.frontuari.callout;

import java.math.BigDecimal;
import java.util.Properties;

import org.adempiere.base.IColumnCallout;
import org.compiere.model.GridField;
import org.compiere.model.GridTab;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class FTU_CalcDiscount implements IColumnCallout {

	@Override
	public String start(Properties ctx, int WindowNo, GridTab mTab,
			GridField mField, Object value, Object oldValue) {
		if(value == null)
			return null;
		if("Discount".equalsIgnoreCase(mField.getColumnName())) {
			if(value == null || mTab.getValue("PriceActual") == null || mTab.getValue("M_Production_ID") == null)
				return null;
			int M_Production_ID = (int) mTab.getValue("M_Production_ID");
			if(M_Production_ID == 0)
				return null;
			BigDecimal discount = (BigDecimal) value;
			BigDecimal pricesLines = BigDecimal.ZERO;
			BigDecimal priceActual = BigDecimal.ZERO;
			int precision = 4;
			String sql = "SELECT SUM(PriceActual) FROM M_ProductionLine WHERE M_Production_ID =? ";
			pricesLines = DB.getSQLValueBD(null, sql, M_Production_ID);
			if(pricesLines == null || pricesLines.compareTo(BigDecimal.ZERO) == 0) {
				pricesLines = BigDecimal.ZERO;
				mTab.setValue("PriceActual", BigDecimal.ZERO);
			}
			discount = discount.divide(Env.ONEHUNDRED, precision, BigDecimal.ROUND_HALF_UP);
			priceActual = pricesLines.multiply(discount.add(BigDecimal.ONE));
			mTab.setValue("PriceActual", priceActual);
		}
		return null;
	}

}
