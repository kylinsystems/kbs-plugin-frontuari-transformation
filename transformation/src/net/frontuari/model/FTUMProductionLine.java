package net.frontuari.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.Util;
import org.compiere.model.I_M_ProductionPlan;
import org.compiere.model.MAttributeSetInstance;
import org.compiere.model.MInvoice;
import org.compiere.model.MLocator;
import org.compiere.model.MProduct;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.MProductionLineMA;
import org.compiere.model.MProductionPlan;
import org.compiere.model.MQualityTest;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MTransaction;
import org.compiere.model.MUOM;
import org.compiere.model.Query;

public class FTUMProductionLine extends MProductionLine{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3374180112348441656L;

	protected FTUMProduction productionParent;

	/**
	 * 	Standard Constructor
	 *	@param ctx ctx
	 *	@param M_ProductionLine_ID id
	 */
	public FTUMProductionLine (Properties ctx, int M_ProductionLine_ID, String trxName)
	{
		super (ctx, M_ProductionLine_ID, trxName);
		if (M_ProductionLine_ID == 0)
		{
			setLine (0);	// @SQL=SELECT NVL(MAX(Line),0)+10 AS DefaultValue FROM M_ProductionLine WHERE M_Production_ID=@M_Production_ID@
			setM_AttributeSetInstance_ID (0);
//			setM_Locator_ID (0);	// @M_Locator_ID@
//			setM_Product_ID (0);
			setM_ProductionLine_ID (0);
			setM_Production_ID (0);
			setMovementQty (Env.ZERO);
			setProcessed (false);
		}
			
	}	// MProductionLine
	
	public FTUMProductionLine (Properties ctx, ResultSet rs, String trxName)
	{
		super(ctx, rs, trxName);
	}	//	MProductionLine
	
	/**
	 * Parent Constructor
	 * @param plan
	 */
	public FTUMProductionLine( FTUMProduction header ) {
		super( header.getCtx(), 0, header.get_TrxName() );
		setM_Production_ID( header.get_ID());
		setAD_Client_ID(header.getAD_Client_ID());
		setAD_Org_ID(header.getAD_Org_ID());
		productionParent = header;
	}
	
	public FTUMProductionLine( MProductionPlan header ) {
		super( header.getCtx(), 0, header.get_TrxName() );
		setM_ProductionPlan_ID( header.get_ID());
		setAD_Client_ID(header.getAD_Client_ID());
		setAD_Org_ID(header.getAD_Org_ID());
	}
	
	
	/**
	 * 
	 * @param date
	 * @return "" for success, error string if failed
	 */
	public String createTransactions(Timestamp date, boolean mustBeStocked) {
		// delete existing ASI records
		int deleted = deleteMA();
		if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Deleted " + deleted + " attribute records ");
		
		MProduct prod = new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
		if (log.isLoggable(Level.FINE))log.log(Level.FINE,"Loaded Product " + prod.toString());
		
		if ( prod.getProductType().compareTo(MProduct.PRODUCTTYPE_Item ) != 0 )  {
			// no need to do any movements
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Production Line " + getLine() + " does not require stock movement");
			return "";
		}
		StringBuilder errorString = new StringBuilder();
		
		MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(), get_TrxName());
		String asiString = asi.getDescription();
		if ( asiString == null )
			asiString = "";
		
		if (log.isLoggable(Level.FINEST))	log.log(Level.FINEST, "asi Description is: " + asiString);
		// create transactions for finished goods
		if ( isEndProduct() ) {
			
			Timestamp dateMPolicy = date;
			if(getM_AttributeSetInstance_ID()>0){
				dateMPolicy = asi.getCreated();
			}
			
			dateMPolicy = Util.removeTime(dateMPolicy);
			MProductionLineMA lineMA = new MProductionLineMA( this,
					asi.get_ID(), getMovementQty(),dateMPolicy);
			if ( !lineMA.save(get_TrxName()) ) {
				log.log(Level.SEVERE, "Could not save MA for " + toString());
				errorString.append("Could not save MA for " + toString() + "\n" );
			}
			MTransaction matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
					"P+", 
					getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
					getMovementQty(), date, get_TrxName());
			matTrx.setM_ProductionLine_ID(get_ID());
			if ( !matTrx.save(get_TrxName()) ) {
				log.log(Level.SEVERE, "Could not save transaction for " + toString());
				errorString.append("Could not save transaction for " + toString() + "\n");
			}
			MStorageOnHand storage = MStorageOnHand.getCreate(getCtx(), getM_Locator_ID(),
					getM_Product_ID(), asi.get_ID(),dateMPolicy, get_TrxName());
			storage.addQtyOnHand(getMovementQty());
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Created finished goods line " + getLine());
			
			return errorString.toString();
		}
		
		// create transactions and update stock used in production
		MStorageOnHand[] storages = MStorageOnHand.getAll( getCtx(), getM_Product_ID(),
				getM_Locator_ID(), get_TrxName(), false, 0);
		
		MProductionLineMA lineMA = null;
		MTransaction matTrx = null;
		BigDecimal qtyToMove = getMovementQty().negate();

		if (qtyToMove.signum() > 0) {
			for (int sl = 0; sl < storages.length; sl++) {
	
				BigDecimal lineQty = storages[sl].getQtyOnHand();
				
				if (log.isLoggable(Level.FINE))log.log(Level.FINE, "QtyAvailable " + lineQty );
				if (lineQty.signum() > 0) 
				{
					if (lineQty.compareTo(qtyToMove ) > 0)
							lineQty = qtyToMove;
	
					MAttributeSetInstance slASI = new MAttributeSetInstance(getCtx(),
							storages[sl].getM_AttributeSetInstance_ID(),get_TrxName());
					String slASIString = slASI.getDescription();
					if (slASIString == null)
						slASIString = "";
					
					if (log.isLoggable(Level.FINEST))log.log(Level.FINEST,"slASI-Description =" + slASIString);
						
					if ( slASIString.compareTo(asiString) == 0
							|| asi.getM_AttributeSet_ID() == 0  )  
					//storage matches specified ASI or is a costing asi (inc. 0)
				    // This process will move negative stock on hand quantities
					{
						lineMA = MProductionLineMA.get(this,storages[sl].getM_AttributeSetInstance_ID(),storages[sl].getDateMaterialPolicy());
						lineMA.setMovementQty(lineMA.getMovementQty().add(lineQty.negate()));
						if ( !lineMA.save(get_TrxName()) ) {
							log.log(Level.SEVERE, "Could not save MA for " + toString());
							errorString.append("Could not save MA for " + toString() + "\n" );
						} else {
							if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved MA for " + toString());
						}
						matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
								"P-", 
								getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
								lineQty.negate(), date, get_TrxName());
						matTrx.setM_ProductionLine_ID(get_ID());
						if ( !matTrx.save(get_TrxName()) ) {
							log.log(Level.SEVERE, "Could not save transaction for " + toString());
							errorString.append("Could not save transaction for " + toString() + "\n");
						} else {
							if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved transaction for " + toString());
						}
						DB.getDatabase().forUpdate(storages[sl], 120);
						storages[sl].addQtyOnHand(lineQty.negate());
						qtyToMove = qtyToMove.subtract(lineQty);
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, getLine() + " Qty moved = " + lineQty + ", Remaining = " + qtyToMove );
					}
				}
				
				if ( qtyToMove.signum() == 0 )			
					break;
				
			} // for available storages
		}
		
		
		if ( !( qtyToMove.signum() == 0) ) {
			if (mustBeStocked && qtyToMove.signum() > 0)
			{
				MLocator loc = new MLocator(getCtx(), getM_Locator_ID(), get_TrxName());
				/*errorString.append( "Insufficient qty on hand of " + prod.toString() + " at "
						+ loc.toString() + "\n");*/
				errorString.append( "No hay suficientes cantidades del producto " + prod.getSKU()+ " " +prod.getName()+ " en la ubicacion "
						+ loc.toString() + "\n");
			}
			else
			{
				MStorageOnHand storage = MStorageOnHand.getCreate(Env.getCtx(), getM_Locator_ID(), getM_Product_ID(),
						asi.get_ID(), date, get_TrxName(), true);
				
				BigDecimal lineQty = qtyToMove;
				MAttributeSetInstance slASI = new MAttributeSetInstance(getCtx(),
						storage.getM_AttributeSetInstance_ID(),get_TrxName());
				String slASIString = slASI.getDescription();
				if (slASIString == null)
					slASIString = "";
				
				if (log.isLoggable(Level.FINEST))log.log(Level.FINEST,"slASI-Description =" + slASIString);
					
				if ( slASIString.compareTo(asiString) == 0
						|| asi.getM_AttributeSet_ID() == 0  )  
				//storage matches specified ASI or is a costing asi (inc. 0)
			    // This process will move negative stock on hand quantities
				{
					lineMA = MProductionLineMA.get(this,storage.getM_AttributeSetInstance_ID(),storage.getDateMaterialPolicy());
					lineMA.setMovementQty(lineMA.getMovementQty().add(lineQty.negate()));
					
					if ( !lineMA.save(get_TrxName()) ) {
						log.log(Level.SEVERE, "Could not save MA for " + toString());
						errorString.append("Could not save MA for " + toString() + "\n" );
					} else {
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved MA for " + toString());
					}
					matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
							"P-", 
							getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
							lineQty.negate(), date, get_TrxName());
					matTrx.setM_ProductionLine_ID(get_ID());
					if ( !matTrx.save(get_TrxName()) ) {
						log.log(Level.SEVERE, "Could not save transaction for " + toString());
						errorString.append("Could not save transaction for " + toString() + "\n");
					} else {
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved transaction for " + toString());
					}
					storage.addQtyOnHand(lineQty.negate());
					qtyToMove = qtyToMove.subtract(lineQty);
					if (log.isLoggable(Level.FINE))log.log(Level.FINE, getLine() + " Qty moved = " + lineQty + ", Remaining = " + qtyToMove );
				} else {
					errorString.append( "Storage doesn't match ASI " + prod.toString() + " / "
							+ slASIString + " vs. " + asiString + "\n");
				}
				
			}
			
		}
			
		return errorString.toString();
		
	}
	
	/*
	public String createTransactions(Timestamp date, boolean mustBeStocked) {
		// delete existing ASI records
		int deleted = deleteMA();
		if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Deleted " + deleted + " attribute records ");
		
		MProduct prod = new MProduct(getCtx(), getM_Product_ID(), get_TrxName());
		if (log.isLoggable(Level.FINE))log.log(Level.FINE,"Loaded Product " + prod.toString());
		
		if ( prod.getProductType().compareTo(MProduct.PRODUCTTYPE_Item ) != 0 )  {
			// no need to do any movements
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Production Line " + getLine() + " does not require stock movement");
			return "";
		}
		StringBuilder errorString = new StringBuilder();
		
		MAttributeSetInstance asi = new MAttributeSetInstance(getCtx(), getM_AttributeSetInstance_ID(), get_TrxName());
		String asiString = asi.getDescription();
		if ( asiString == null )
			asiString = "";
		
		if (log.isLoggable(Level.FINEST))	log.log(Level.FINEST, "asi Description is: " + asiString);
		// create transactions for finished goods
		if ( getM_Product_ID() == getEndProduct_ID()) {
			
			Timestamp dateMPolicy = date;
			if(getM_AttributeSetInstance_ID()>0){
				dateMPolicy = asi.getCreated();
			}
			
			dateMPolicy = Util.removeTime(dateMPolicy);
			MProductionLineMA lineMA = new MProductionLineMA( this,
					asi.get_ID(), getMovementQty(),dateMPolicy);
			if ( !lineMA.save(get_TrxName()) ) {
				log.log(Level.SEVERE, "Could not save MA for " + toString());
				errorString.append("Could not save MA for " + toString() + "\n" );
			}
			MTransaction matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
					"P+", 
					getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
					getMovementQty(), date, get_TrxName());
			matTrx.setM_ProductionLine_ID(get_ID());
			if ( !matTrx.save(get_TrxName()) ) {
				log.log(Level.SEVERE, "Could not save transaction for " + toString());
				errorString.append("Could not save transaction for " + toString() + "\n");
			}
			MStorageOnHand storage = MStorageOnHand.getCreate(getCtx(), getM_Locator_ID(),
					getM_Product_ID(), asi.get_ID(),dateMPolicy, get_TrxName());
			storage.addQtyOnHand(getMovementQty());
			storage.saveEx(get_TrxName());
			if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Created finished goods line " + getLine());
			
			return errorString.toString();
		}
		
		// create transactions and update stock used in production
		MStorageOnHand[] storages = MStorageOnHand.getAll( getCtx(), getM_Product_ID(),
				getM_Locator_ID(), get_TrxName(), false, 0);
		
		MProductionLineMA lineMA = null;
		MTransaction matTrx = null;
		BigDecimal qtyToMove = getMovementQty().negate();

		if (qtyToMove.signum() > 0) {
			for (int sl = 0; sl < storages.length; sl++) {
	
				BigDecimal lineQty = storages[sl].getQtyOnHand();
				
				if (log.isLoggable(Level.FINE))log.log(Level.FINE, "QtyAvailable " + lineQty );
				if (lineQty.signum() > 0) 
				{
					if (lineQty.compareTo(qtyToMove ) > 0)
							lineQty = qtyToMove;
	
					MAttributeSetInstance slASI = new MAttributeSetInstance(getCtx(),
							storages[sl].getM_AttributeSetInstance_ID(),get_TrxName());
					String slASIString = slASI.getDescription();
					if (slASIString == null)
						slASIString = "";
					
					if (log.isLoggable(Level.FINEST))log.log(Level.FINEST,"slASI-Description =" + slASIString);
						
					if ( slASIString.compareTo(asiString) == 0
							|| asi.getM_AttributeSet_ID() == 0  )  
					//storage matches specified ASI or is a costing asi (inc. 0)
				    // This process will move negative stock on hand quantities
					{
						lineMA = MProductionLineMA.get(this,storages[sl].getM_AttributeSetInstance_ID(),storages[sl].getDateMaterialPolicy());
						lineMA.setMovementQty(lineMA.getMovementQty().add(lineQty.negate()));
						if ( !lineMA.save(get_TrxName()) ) {
							log.log(Level.SEVERE, "Could not save MA for " + toString());
							errorString.append("Could not save MA for " + toString() + "\n" );
						} else {
							if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved MA for " + toString());
						}
						matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
								"P-", 
								getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
								lineQty.negate(), date, get_TrxName());
						matTrx.setM_ProductionLine_ID(get_ID());
						if ( !matTrx.save(get_TrxName()) ) {
							log.log(Level.SEVERE, "Could not save transaction for " + toString());
							errorString.append("Could not save transaction for " + toString() + "\n");
						} else {
							if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved transaction for " + toString());
						}
						DB.getDatabase().forUpdate(storages[sl], 120);
						storages[sl].addQtyOnHand(lineQty.negate());
						qtyToMove = qtyToMove.subtract(lineQty);
						storages[sl].saveEx(get_TrxName());
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, getLine() + " Qty moved = " + lineQty + ", Remaining = " + qtyToMove );
					}
				}
				
				if ( qtyToMove.signum() == 0 )			
					break;
				
			} // for available storages
		}
		
		
		if ( !( qtyToMove.signum() == 0) ) {
			if (mustBeStocked && qtyToMove.signum() > 0)
			{
				MLocator loc = new MLocator(getCtx(), getM_Locator_ID(), get_TrxName());
				//errorString.append( "Insufficient qty on hand of " + prod.toString() + " at "
					//	+ loc.toString() + "\n");
				MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_InsufficientQtyOH"));
				Object[] arguments = new Object[]{prod.getName(), loc.toString()};
				errorString.append(mf.format(arguments) + "\n");
			
			}
			else
			{
				MStorageOnHand storage = MStorageOnHand.getCreate(Env.getCtx(), getM_Locator_ID(), getM_Product_ID(),
						asi.get_ID(), date, get_TrxName(), true);
				
				BigDecimal lineQty = qtyToMove;
				MAttributeSetInstance slASI = new MAttributeSetInstance(getCtx(),
						storage.getM_AttributeSetInstance_ID(),get_TrxName());
				String slASIString = slASI.getDescription();
				if (slASIString == null)
					slASIString = "";
				
				if (log.isLoggable(Level.FINEST))log.log(Level.FINEST,"slASI-Description =" + slASIString);
					
				if ( slASIString.compareTo(asiString) == 0
						|| asi.getM_AttributeSet_ID() == 0  )  
				//storage matches specified ASI or is a costing asi (inc. 0)
			    // This process will move negative stock on hand quantities
				{
					lineMA = MProductionLineMA.get(this,storage.getM_AttributeSetInstance_ID(),storage.getDateMaterialPolicy());
					lineMA.setMovementQty(lineMA.getMovementQty().add(lineQty.negate()));
					
					if ( !lineMA.save(get_TrxName()) ) {
						log.log(Level.SEVERE, "Could not save MA for " + toString());
						errorString.append("Could not save MA for " + toString() + "\n" );
					} else {
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved MA for " + toString());
					}
					matTrx = new MTransaction (getCtx(), getAD_Org_ID(), 
							"P-", 
							getM_Locator_ID(), getM_Product_ID(), asi.get_ID(), 
							lineQty.negate(), date, get_TrxName());
					matTrx.setM_ProductionLine_ID(get_ID());
					if ( !matTrx.save(get_TrxName()) ) {
						log.log(Level.SEVERE, "Could not save transaction for " + toString());
						errorString.append("Could not save transaction for " + toString() + "\n");
					} else {
						if (log.isLoggable(Level.FINE))log.log(Level.FINE, "Saved transaction for " + toString());
					}
					storage.addQtyOnHand(lineQty.negate());
					qtyToMove = qtyToMove.subtract(lineQty);
					storage.saveEx(get_TrxName());
					if (log.isLoggable(Level.FINE))log.log(Level.FINE, getLine() + " Qty moved = " + lineQty + ", Remaining = " + qtyToMove );
				} else {
					errorString.append( "Storage doesn't match ASI " + prod.toString() + " / "
							+ slASIString + " vs. " + asiString + "\n");
				}
				
			}
			
		}
			
		return errorString.toString();
		
	}*/

	protected int getEndProduct_ID() {
		if (productionParent != null) {
			return productionParent.getM_Product_ID();
		} else if (getM_Production_ID() > 0) {
			return getM_Production().getM_Product_ID();
		} else {
			return getM_ProductionPlan().getM_Product_ID();
		}
	}

	protected int deleteMA() {
		String sql = "DELETE FROM M_ProductionLineMA WHERE M_ProductionLine_ID = " + get_ID();
		int count = DB.executeUpdateEx( sql, get_TrxName() );
		return count;
	}

	public String toString() {
		if ( getM_Product_ID() == 0 )
			return ("No product defined for production line " + getLine());
		MProduct product = new MProduct(getCtx(),getM_Product_ID(), get_TrxName());
		return ( "Production line:" + getLine() + " -- " + getMovementQty() + " of " + product.getValue());
	}
 
	
	
	@Override
	protected boolean beforeSave(boolean newRecord) 
	{
		if (productionParent == null && getM_Production_ID() > 0)
			productionParent = new FTUMProduction(getCtx(), getM_Production_ID(), get_TrxName());
		BigDecimal qtyused = getQtyUsed();
		if(qtyused==null||qtyused.compareTo(BigDecimal.ZERO)==0){
			setQtyUsed(BigDecimal.ZERO);
		}
		int C_UOM_ID = get_ValueAsInt("C_UOM_ID");
		MProduct prod = new MProduct(getCtx(),getM_Product_ID(),get_TrxName());
		String trxType = productionParent.get_ValueAsString("TrxType");
		 
		boolean isTransformation = trxType.equalsIgnoreCase("T");
		
		BigDecimal cost = BigDecimal.ZERO;
		
		if (getM_Production_ID() > 0) 
		{
			if(isTransformation){
				BigDecimal movementQty = new BigDecimal(0);
				if ( productionParent.getM_Product_ID() == getM_Product_ID()) {
					
					movementQty = getQtyUsed().negate();
					if(C_UOM_ID>0){
						if(prod.getC_UOM_ID()!=C_UOM_ID) {
							String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
							BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
							if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {						
								movementQty = movementQty.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
								setMovementQty(movementQty);
							}
						}else
							setMovementQty(movementQty);
					}else
						setMovementQty(movementQty);

					if(is_ValueChanged("M_Product_ID")) {
						cost = getPurchaseProductCost(productionParent.getM_Product_ID(),getAD_Org_ID());
						set_ValueOfColumn("PriceCost", cost);
					}
				}else {		
					BigDecimal conversionFactor = get_Value("MultiplyRate")!=null?(BigDecimal)get_Value("MultiplyRate"):BigDecimal.ZERO ;
					
					BigDecimal qtyUsed = getQtyUsed();
					
					movementQty = conversionFactor.multiply(qtyUsed).setScale(2, RoundingMode.HALF_UP);
					
					if(C_UOM_ID>0){
						if(prod.getC_UOM_ID()!=C_UOM_ID) {
							String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
							BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
							if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {						
								movementQty = movementQty.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
								setMovementQty(movementQty);
							}
						}else
							setMovementQty(movementQty);
					}else
						setMovementQty(movementQty);

					if(is_ValueChanged("M_Product_ID")) {
						cost = getPurchaseProductCost(productionParent.getM_Product_ID(),getAD_Org_ID());
						cost = cost.divide((conversionFactor != null && conversionFactor.compareTo(BigDecimal.ZERO) > 0)?conversionFactor:BigDecimal.ONE,4, RoundingMode.HALF_UP);
						if(cost.compareTo(BigDecimal.ZERO)>0)
							set_ValueOfColumn("PriceCost", cost);
					}
					
				}
				
			}else {

				if ( productionParent.getM_Product_ID() == getM_Product_ID() && productionParent.getProductionQty().signum() == getMovementQty().signum()) {
					
					if(C_UOM_ID>0){
						if(prod.getC_UOM_ID()!=C_UOM_ID) {
							String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
							BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
							if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {
								BigDecimal base = getPlannedQty();
								base = base.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
								setMovementQty(base);
							}
						}
					}
										
					setIsEndProduct(true);
				}else {
					if(C_UOM_ID>0){
						if(prod.getC_UOM_ID()!=C_UOM_ID) {
							String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
							BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
							if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {
								BigDecimal base = getQtyUsed();
								base = base.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
								setMovementQty(base.negate());
							}
						}else
							setMovementQty(getQtyUsed().negate());
					}else
						setMovementQty(getQtyUsed().negate());
					
					if(is_ValueChanged("M_Product_ID")) {
						cost = getPurchaseProductCost(getM_Product_ID(),getAD_Org_ID());
						set_ValueOfColumn("PriceCost", cost);
					}
					setIsEndProduct(false);
				}
				
				String sqlU = "UPDATE M_ProductionLine pl SET PriceCost = pf.PriceCost/pf.productionqty FROM "
						+ " (SELECT SUM(ppl.PriceCost*(ppl.movementqty*-1)) AS PriceCost, COALESCE(MAX(pp.productionqty),1) AS productionqty FROM M_ProductionLine ppl "
						+ " JOIN M_Production pp ON ppl.M_Production_ID=pp.M_Production_ID"
						+ " WHERE ppl.M_Product_ID <> "+ productionParent.getM_Product_ID() +" AND ppl.M_Production_ID = "+getM_Production_ID()+") pf"
						+ " WHERE pl.M_Product_ID = "+ productionParent.getM_Product_ID() +" AND pl.M_Production_ID = " + getM_Production_ID();
				int cont;
				if(is_ValueChanged("M_Product_ID")||is_ValueChanged("QtyUsed")||is_ValueChanged("movementqty")||is_new())
					cont = DB.executeUpdate(sqlU, get_TrxName());
				
				
			}
		} 
		else 
		{
			I_M_ProductionPlan plan = getM_ProductionPlan();
			if (plan.getM_Product_ID() == getM_Product_ID() && plan.getProductionQty().signum() == getMovementQty().signum())
				setIsEndProduct(true);
			else 
				setIsEndProduct(false);
		}
		
		if ( isEndProduct() && getM_AttributeSetInstance_ID() != 0 )
		{
			String where = "M_QualityTest_ID IN (SELECT M_QualityTest_ID " +
			"FROM M_Product_QualityTest WHERE M_Product_ID=?) " +
			"AND M_QualityTest_ID NOT IN (SELECT M_QualityTest_ID " +
			"FROM M_QualityTestResult WHERE M_AttributeSetInstance_ID=?)";

			List<MQualityTest> tests = new Query(getCtx(), MQualityTest.Table_Name, where, get_TrxName())
			.setOnlyActiveRecords(true).setParameters(getM_Product_ID(), getM_AttributeSetInstance_ID()).list();
			// create quality control results
			for (MQualityTest test : tests)
			{
				test.createResult(getM_AttributeSetInstance_ID());
			}
		}
		
		
		/*if ( !isEndProduct() && !isTransformation)
		{
			if(C_UOM_ID>0){
				if(prod.getC_UOM_ID()!=C_UOM_ID) {
					String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
					BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
					if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {
						BigDecimal base = getQtyUsed();
						base = base.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
						setMovementQty(base.negate());
					}
				}else
					setMovementQty(getQtyUsed().negate());
			}else
				setMovementQty(getPlannedQty().negate());
		}else if(isEndProduct() && isTransformation){			
			BigDecimal movementQty = new BigDecimal(0);
			if(productionParent.getM_Product_ID()==getM_Product_ID()){
				
				movementQty = getQtyUsed();
				
			}else if (isTransformation){
				
			BigDecimal conversionFactor = get_Value("MultiplyRate")!=null?(BigDecimal)get_Value("MultiplyRate"):BigDecimal.ZERO ;
			
			BigDecimal qtyUsed = getQtyUsed();
			
			movementQty = conversionFactor.multiply(qtyUsed);
			
			if(C_UOM_ID>0){
				if(prod.getC_UOM_ID()!=C_UOM_ID) {
					String sql = "SELECT DivideRate FROM C_UOM_Conversion WHERE M_Product_ID="+prod.getM_Product_ID()+" AND C_UOM_ID="+prod.getC_UOM_ID()+" AND C_UOM_To_ID="+C_UOM_ID;
					BigDecimal multiplyrate = DB.getSQLValueBD(get_TrxName(), sql);
					if(multiplyrate.compareTo(BigDecimal.ZERO)>0) {						
						movementQty = movementQty.multiply(multiplyrate).setScale(2, RoundingMode.HALF_UP);
						setMovementQty(movementQty);
					}
				}else
					setMovementQty(movementQty);
			}else
				setMovementQty(movementQty);
			
			}
		}*/
		
		return true;
	}
	
	
	public BigDecimal getPurchaseProductCost (int M_Product_ID, int AD_Org_ID) {
		String sql = "select price from ftu_lastproductsdocuments where issotrx = 'N' and ad_org_id = "+AD_Org_ID+" and m_product_id = "+M_Product_ID 
				+ " and price>0 and ad_table_id in ("+MInvoice.Table_ID+","+MProduction.Table_ID+")"
				+ " order by datedoc desc, updated desc";
		BigDecimal cost = DB.getSQLValueBD(get_TrxName(), sql);
		if(cost!=null && cost.compareTo(BigDecimal.ZERO)>0)
			return cost;
		
		return BigDecimal.ZERO;
	}
	
	
	

	@Override
	protected boolean beforeDelete() {
		
		deleteMA();
		return true;
	}
	
	
}
