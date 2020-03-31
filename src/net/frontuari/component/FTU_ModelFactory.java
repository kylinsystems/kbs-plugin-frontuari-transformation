package net.frontuari.component;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.util.Properties;

import org.adempiere.base.IModelFactory;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.PO;
import org.compiere.util.Env;

import net.frontuari.model.FTUMProduction;
import net.frontuari.model.FTUMProductionLine;

public class FTU_ModelFactory implements IModelFactory {

	@Override
	public Class<?> getClass(String tableName) {
		if(tableName.equalsIgnoreCase(MProduction.Table_Name)) 
			return FTUMProduction.class;
		if(tableName.equalsIgnoreCase(MProductionLine.Table_Name))
			return FTUMProductionLine.class;
		return null;
	}

	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		
		if(tableName.equals(MProduction.Table_Name)){
			return new FTUMProduction(Env.getCtx(),Record_ID, trxName);
		}else if(tableName.equals(MProductionLine.Table_Name)){
			return new FTUMProductionLine(Env.getCtx(),Record_ID, trxName);
		}
		/*
		Class<?> clazz = getClass(tableName);
		
		if(clazz!=null)
		{
			try 
			{
				return (PO) clazz.getConstructor(Properties.class, int.class, String.class).newInstance(Env.getCtx(),Record_ID, trxName);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	*/
		return null;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		
		if(tableName.equals(MProduction.Table_Name)){
			return new FTUMProduction(Env.getCtx(),rs, trxName);
		}else if(tableName.equals(MProductionLine.Table_Name)){
			return new FTUMProductionLine(Env.getCtx(),rs, trxName);
		}
		/*
		 * Class<?> clazz = getClass(tableName);
		
		if(clazz!=null)
		{
			try 
			{
				return (PO) clazz.getConstructor(Properties.class, ResultSet.class, String.class).newInstance(Env.getCtx(),rs, trxName);
			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
		return null;
	}

}
