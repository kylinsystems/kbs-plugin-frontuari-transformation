package net.frontuari.component;

import net.frontuari.callout.FTU_SetProductionValues;

import org.adempiere.base.IColumnCallout;
import org.adempiere.base.IColumnCalloutFactory;
import net.frontuari.model.FTUMProduction;
import net.frontuari.model.FTUMProductionLine;

//import com.bucaresystems.callout.BSCA_SetProductionValues;

public class CalloutFactory implements IColumnCalloutFactory {

	@Override
	public IColumnCallout[] getColumnCallouts(String tableName,
			String columnName) {
		if(tableName.equalsIgnoreCase(FTUMProduction.Table_Name)) {
			if(columnName.equalsIgnoreCase(FTUMProduction.COLUMNNAME_M_Product_ID))
				return new IColumnCallout[] { new FTU_SetProductionValues() };
				//return new IColumnCallout[] { new BSCA_SetProductionValues() };
		}
		return null;
	}

}
