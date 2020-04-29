package net.frontuari.acct;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.compiere.acct.DocLine;
import org.compiere.acct.Doc_Production;
import org.compiere.acct.Fact;
import org.compiere.acct.FactLine;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MCostDetail;
import org.compiere.model.MCurrency;
import org.compiere.model.MFactAcct;
import org.compiere.model.ProductCost;
import org.compiere.model.X_M_Production;
import org.compiere.model.X_M_ProductionLine;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class Doc_Transformation extends Doc_Production{
	
	private Map<Integer, BigDecimal> mQtyProduced;
	

	public Doc_Transformation(MAcctSchema as, ResultSet rs, String trxName) {
		super(as, rs, trxName);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *  Load Document Details
	 *  @return error message or null
	 */
	protected String loadDocumentDetails()
	{
		setC_Currency_ID (NO_CURRENCY);
		X_M_Production prod = (X_M_Production)getPO();
		setDateDoc (prod.getMovementDate());
		setDateAcct(prod.getMovementDate());
		//	Contained Objects
		p_lines = loadLines(prod);
		if (log.isLoggable(Level.FINE)) log.fine("Lines=" + p_lines.length);
		return null;
	}   //  loadDocumentDetails

	
	
	/**
	 *	Load Invoice Line
	 *	@param prod production
	 *  @return DoaLine Array
	 */
	private DocLine[] loadLines(X_M_Production prod)
	{
		ArrayList<DocLine> list = new ArrayList<DocLine>();
		mQtyProduced = new HashMap<>(); 
		String sqlPL = null;
		if (prod.isUseProductionPlan()){
//			Production
			//	-- ProductionLine	- the real level
			sqlPL = "SELECT * FROM "
							+ " M_ProductionLine pro_line INNER JOIN M_ProductionPlan plan ON pro_line.M_ProductionPlan_id = plan.M_ProductionPlan_id "
							+ " INNER JOIN M_Production pro ON pro.M_Production_id = plan.M_Production_id "
							+ " WHERE pro.M_Production_ID=? "
							+ " ORDER BY plan.M_ProductionPlan_id, pro_line.Line";
		}else{
//			Production
			//	-- ProductionLine	- the real level
			sqlPL = "SELECT * FROM M_ProductionLine pl "
					+ "WHERE pl.M_Production_ID=? "
					+ "ORDER BY pl.Line";
		}
		
		PreparedStatement pstmtPL = null;
		ResultSet rsPL = null;
		try
		{			
			pstmtPL = DB.prepareStatement(sqlPL, getTrxName());
			pstmtPL.setInt(1,get_ID());
			rsPL = pstmtPL.executeQuery();
			while (rsPL.next())
			{
				X_M_ProductionLine line = new X_M_ProductionLine(getCtx(), rsPL, getTrxName());
				if (line.getMovementQty().signum() == 0)
				{
					if (log.isLoggable(Level.INFO)) log.info("LineQty=0 - " + line);
					continue;
				}
				DocLine docLine = new DocLine (line, this);
				docLine.setQty (line.getMovementQty(), false);
				//	Identify finished BOM Product
				if (prod.isUseProductionPlan())
					docLine.setProductionBOM(line.getM_Product_ID() == line.getM_ProductionPlan().getM_Product_ID());
				else
					docLine.setProductionBOM(line.getM_Product_ID() == prod.getM_Product_ID());
				
				if (docLine.isProductionBOM()){
					manipulateQtyProduced (mQtyProduced, line, prod.isUseProductionPlan(), line.getMovementQty());
				}
				//
				if (log.isLoggable(Level.FINE)) log.fine(docLine.toString());
				list.add (docLine);
			}
		}
		catch (Exception ee)
		{
			log.log(Level.SEVERE, sqlPL, ee);
		}
		finally
		{
			DB.close(rsPL, pstmtPL);
			rsPL = null;
			pstmtPL = null;
		}
			
		DocLine[] dl = new DocLine[list.size()];
		list.toArray(dl);
		return dl;
	}	//	loadLines
	
	
	/**
	 * IDEMPIERE-3082
	 * @param mQtyProduced
	 * @param line
	 * @param isUsePlan
	 * @param addMoreQty when you want get value, just pass null
	 * @return
	 */
	private BigDecimal manipulateQtyProduced (Map<Integer, BigDecimal> mQtyProduced, X_M_ProductionLine line, Boolean isUsePlan, BigDecimal addMoreQty){
		BigDecimal qtyProduced = null;
		Integer key = isUsePlan?line.getM_ProductionPlan_ID():line.getM_Production_ID();
		
		if (mQtyProduced.containsKey(key)){
			qtyProduced = mQtyProduced.get(key);
		}else{
			qtyProduced = BigDecimal.ZERO;
			mQtyProduced.put(key, qtyProduced);
		}
		
		if (addMoreQty != null){
			qtyProduced = qtyProduced.add(addMoreQty);
			mQtyProduced.put(key, qtyProduced);
		}
			
		return qtyProduced;
	}
	
	
	@Override
	/**
	 *  Create Facts (the accounting logic) for
	 *  MMP.
	 *  <pre>
	 *  Production
	 *      Inventory       DR      CR
	 *  </pre>
	 *  @param as account schema
	 *  @return Fact
	 */
	public ArrayList<Fact> createFacts (MAcctSchema as)
	{
		//  create Fact Header
		MCurrency curr = new MCurrency(getCtx(),Env.getContextAsInt(getCtx(), "$C_Currency_ID"),getTrxName());
		Fact fact = new Fact(this, as, Fact.POST_Actual);
		setC_Currency_ID (as.getC_Currency_ID());

		//  Line pointer
		FactLine fl = null;
		X_M_Production prod = (X_M_Production)getPO();
		
		DocLine parentLine = null;

		boolean isTransformation = false;
		if(prod.get_ValueAsBoolean("IsTransformation")){
			isTransformation = true ;
			for (int i = 0; i < p_lines.length; i++){
					  DocLine line = p_lines[i];
					  if(line.getProduct().getM_Product_ID()==prod.getM_Product_ID()){
						  parentLine=p_lines[i];
					  }
				  }
		}else{
			for (int i = 0; i < p_lines.length; i++){
				  DocLine line = p_lines[i];
				  if(line.getProduct().getM_Product_ID()==prod.getM_Product_ID()){
					  parentLine=p_lines[i];
				  }
			  }
		}	
		
		for (int i = 0; i < p_lines.length; i++){
		  if(isTransformation){
			  DocLine line = p_lines[i];
			  //	Calculate Costs
			  BigDecimal costs = null;
			  
			  MCostDetail cd = MCostDetail.get (as.getCtx(), "M_ProductionLine_ID=?",
					  parentLine.get_ID(), parentLine.getM_AttributeSetInstance_ID(), as.getC_AcctSchema_ID(), getTrxName());
				if (cd != null) {
					
					costs = cd.getAmt();
					if(parentLine.getM_Product_ID()!=line.getM_Product_ID()){
						//costs = costs.negate();
						X_M_ProductionLine prodLine = (X_M_ProductionLine)line.getPO();
						//X_M_ProductionLine prodParentLine = (X_M_ProductionLine)parentLine.getPO();
						
						
						//if(prodLine.get_ValueAsBoolean("IsEndProduct")){
							BigDecimal qtyUsed = prodLine.getQtyUsed();
						//	BigDecimal qtyParentUsed = prodParentLine.getQtyUsed();
							
							
							if(qtyUsed.signum()==0){
								costs = costs.subtract(costs);
							}else if(qtyUsed.signum()<0 || qtyUsed.signum()>0){
							costs = (BigDecimal) prodLine.get_Value("PriceActual");
							BigDecimal movementQty= prodLine.getMovementQty();
							costs = costs.multiply(movementQty);
								
							}
							/*if(qtyUsed.signum()==0){
								costs = costs.subtract(costs);
							}else if(qtyUsed.signum()<0 || qtyUsed.signum()>0){
								costs = costs.divide(qtyParentUsed,curr.getCostingPrecision(),RoundingMode.HALF_UP);
								
								costs = costs.multiply(qtyUsed).setScale(curr.getCostingPrecision(),BigDecimal.ROUND_HALF_UP);
							}*/
					}
					
				} else {
					//costs = parentLine.getProductCosts(as, line.getAD_Org_ID(), false).setScale(curr.getCostingPrecision(),BigDecimal.ROUND_HALF_UP);
					/*X_M_ProductionLine endProLine = (X_M_ProductionLine)line.getPO();
					costs = costs.divide(divisor);*/
					//if(parentLine.getM_Product_ID()!=line.getM_Product_ID()){
						//costs = costs.negate();
						
						X_M_ProductionLine prodLine = (X_M_ProductionLine)line.getPO();
						//X_M_ProductionLine prodParentLine = (X_M_ProductionLine)parentLine.getPO();
						BigDecimal qtyUsed = prodLine.getQtyUsed();
						/*
						//if(prodLine.get_ValueAsBoolean("IsEndProduct")){
							
							BigDecimal qtyParentUsed = prodParentLine.getQtyUsed();
							
							
							
								costs = costs.divide(qtyParentUsed,curr.getCostingPrecision(),RoundingMode.HALF_UP);
								
								costs = costs.multiply(qtyUsed).setScale(curr.getCostingPrecision(),BigDecimal.ROUND_HALF_UP);
							}
							*/
						if(qtyUsed.signum()==0){
							costs = BigDecimal.ZERO;
						}else if(qtyUsed.signum()<0 || qtyUsed.signum()>0){
						costs = (BigDecimal) prodLine.get_Value("PriceActual");
						BigDecimal movementQty= prodLine.getMovementQty();
						costs = costs.multiply(movementQty);
							
						}
					if(parentLine.getM_Product_ID()==line.getM_Product_ID()){
						costs = costs.negate();
					}
										
					
				}
				
					
					
				//  Inventory       DR      CR
					fl = fact.createLine(line,
						line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
						as.getC_Currency_ID(), costs);
					if (fl == null)
					{
						p_Error = "No Costs for Line " + line.getLine() + " - " + line;
						return null;
					}
					fl.setM_Locator_ID(line.getM_Locator_ID());
					fl.setQty(line.getQty());

					//	Cost Detail
					String description = line.getDescription();
					if (description == null)
						description = "";
					//if (line.isProductionBOM())
						//description += "(*)";
					if (!MCostDetail.createProduction(as, line.getAD_Org_ID(),
						line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
						line.get_ID(), 0,
						costs, line.getQty(),
						description, getTrxName()))
					{
						p_Error = "Failed to create cost detail record";
						return null;
					}

				//}

		  }else{
			  
			DocLine line = p_lines[i];
			//	Calculate Costs
			BigDecimal costs = null;

			// MZ Goodwill
			// if Production CostDetail exist then get Cost from Cost Detail
			MCostDetail cd = MCostDetail.get (as.getCtx(), "M_ProductionLine_ID=?",
					line.get_ID(), line.getM_AttributeSetInstance_ID(), as.getC_AcctSchema_ID(), getTrxName());
			if (cd != null) {
				costs = cd.getAmt();
			} else {
				costs = line.getProductCosts(as, line.getAD_Org_ID(), false);
			}
			if (line.isProductionBOM())
			{
				X_M_ProductionLine endProLine = (X_M_ProductionLine)line.getPO();
				Object parentEndPro = prod.isUseProductionPlan()?endProLine.getM_ProductionPlan_ID():endProLine.getM_Production_ID();
				
				//	Get BOM Cost - Sum of individual lines
				BigDecimal bomCost = Env.ZERO;
				for (int ii = 0; ii < p_lines.length; ii++)
				{
					DocLine line0 = p_lines[ii];
					X_M_ProductionLine bomProLine = (X_M_ProductionLine)line0.getPO();
					Object parentBomPro = prod.isUseProductionPlan()?bomProLine.getM_ProductionPlan_ID():bomProLine.getM_Production_ID();
					
					if (!parentBomPro.equals(parentEndPro))
						continue;
					if (!line0.isProductionBOM()) {
						// get cost of children
						MCostDetail cd0 = MCostDetail.get (as.getCtx(), "M_ProductionLine_ID=?",
								line0.get_ID(), line0.getM_AttributeSetInstance_ID(), as.getC_AcctSchema_ID(), getTrxName());
						BigDecimal costs0;
						if (cd0 != null) {
							costs0 = cd0.getAmt();
						} else {
							costs0 = line0.getProductCosts(as, line0.getAD_Org_ID(), false);
						}
						bomCost = bomCost.add(costs0.setScale(curr.getCostingPrecision(),BigDecimal.ROUND_HALF_UP));
					}
				}
				
				BigDecimal qtyProduced = manipulateQtyProduced (mQtyProduced, endProLine, prod.isUseProductionPlan(), null);
				if (line.getQty().compareTo(qtyProduced) != 0) {
					BigDecimal factor = line.getQty().divide(qtyProduced, curr.getCostingPrecision(), BigDecimal.ROUND_HALF_UP);
					bomCost = bomCost.multiply(factor).setScale(curr.getCostingPrecision(),BigDecimal.ROUND_HALF_UP);
				}
				int precision = as.getStdPrecision();
				BigDecimal variance = new BigDecimal(0);
				//for transformation process don exist variance
				if(isTransformation){
					//variance = (costs.setScale(precision, BigDecimal.ROUND_HALF_UP)).subtract(bomCost.negate());
				}else{
					variance = (costs.setScale(precision, BigDecimal.ROUND_HALF_UP)).subtract(bomCost.negate());
				}
				// only post variance if it's not zero 
				if (variance.signum() != 0) 
				{
					//post variance 
					fl = fact.createLine(line, 
							line.getAccount(ProductCost.ACCTTYPE_P_RateVariance, as),
							as.getC_Currency_ID(), variance.negate()); 
					if (fl == null) 
					{ 
						p_Error = "Couldn't post variance " + line.getLine() + " - " + line; 
						return null; 
					}
					fl.setQty(Env.ZERO);
				}
				// costs = bomCost.negate();
			}
			// end MZ

			//  Inventory       DR      CR
			fl = fact.createLine(line,
				line.getAccount(ProductCost.ACCTTYPE_P_Asset, as),
				as.getC_Currency_ID(), costs);
			if (fl == null)
			{
				p_Error = "No Costs for Line " + line.getLine() + " - " + line;
				return null;
			}
			fl.setM_Locator_ID(line.getM_Locator_ID());
			fl.setQty(line.getQty());

			//	Cost Detail
			String description = line.getDescription();
			if (description == null)
				description = "";
			if (line.isProductionBOM())
				description += "(*)";
			if (!MCostDetail.createProduction(as, line.getAD_Org_ID(),
				line.getM_Product_ID(), line.getM_AttributeSetInstance_ID(),
				line.get_ID(), 0,
				costs, line.getQty(),
				description, getTrxName()))
			{
				p_Error = "Failed to create cost detail record";
				return null;
			}
		  }
		}
		
		if(!fact.isSourceBalanced()){
			BigDecimal diff = getSourceBalance(fact);
			 FactLine line = new FactLine (getCtx(), get_Table_ID(), 
						get_ID(), 0, getTrxName());
					line.setDocumentInfo(this, null);
					line.setPostingType(MFactAcct.POSTINGTYPE_Actual);

				//	Account
				line.setAccount(as, parentLine.getAccount(ProductCost.ACCTTYPE_P_Cogs, as));//ACCTTYPE_P_CostOfProduction

				//  Amount
				if (diff.signum() < 0)   //  negative balance => DR
					line.setAmtSource(this.getC_Currency_ID(), diff.abs(), Env.ZERO);
				else                                //  positive balance => CR
					line.setAmtSource(this.getC_Currency_ID(), Env.ZERO, diff);
					
				//  Convert
				line.convert();
				//
				if (log.isLoggable(Level.FINE)) log.fine(line.toString());
				fact.add(line);
		}
		//
		ArrayList<Fact> facts = new ArrayList<Fact>();
		facts.add(fact);
		return facts;
	}   //  createFact
	
	protected BigDecimal getSourceBalance(Fact fact)
	{
		BigDecimal result = Env.ZERO;
		for (FactLine line :fact.getLines())
		{
			
			result = result.add (line.getSourceBalance());
		}
	//	log.fine("getSourceBalance - " + result.toString());
		return result;
	}	//	getSourceBalance

}
