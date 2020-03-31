package net.frontuari.component;


import net.frontuari.base.FTUModelFactory;
import net.frontuari.model.FTUMProduction;
import net.frontuari.model.FTUMProductionLine;

import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;

public class ModelFactory extends FTUModelFactory {

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		registerModel(MProductionLine.Table_Name, FTUMProductionLine.class);
		registerModel(MProduction.Table_Name, FTUMProduction.class);
		
	}
	
	

}
