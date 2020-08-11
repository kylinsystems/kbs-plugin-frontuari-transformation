package net.frontuari.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.compiere.acct.Doc;
import org.compiere.model.I_M_ProductionPlan;
import org.compiere.model.MDocType;
import org.compiere.model.MLocator;
import org.compiere.model.MOrderLine;
import org.compiere.model.MPeriod;
import org.compiere.model.MProduct;
import org.compiere.model.MProduction;
import org.compiere.model.MProductionLine;
import org.compiere.model.MProductionPlan;
import org.compiere.model.MProject;
import org.compiere.model.MProjectLine;
import org.compiere.model.MSequence;
import org.compiere.model.MSysConfig;
import org.compiere.model.MWarehouse;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocumentEngine;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Util;

import com.bucaresystems.model.MBSCAPriceChange;


public class FTUMProduction extends MProduction implements DocAction {


	/**
	 * 
	 */
	private static final long serialVersionUID = -3518007409275232139L;
	
	/**	Process Message 			*/
	public String		m_processMsg = null;
	/**	Just Prepared Flag			*/
	public boolean		m_justPrepared = false;
	

	/**
	 * 
	 */
	/** Log								*/
	@SuppressWarnings("unused")
	private static CLogger		m_log = CLogger.getCLogger (MProduction.class);
	private int lineno;
	private int count;

	public FTUMProduction(Properties ctx, int M_Production_ID, String trxName) {
		super(ctx, M_Production_ID, trxName);
		if (M_Production_ID == 0) {
			setDocStatus(DOCSTATUS_Drafted);
			setDocAction (DOCACTION_Prepare);
		}
	}

	public FTUMProduction(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public FTUMProduction( MOrderLine line ) {
		super( line.getCtx(), 0, line.get_TrxName());
		setAD_Client_ID(line.getAD_Client_ID());
		setAD_Org_ID(line.getAD_Org_ID());
		setMovementDate( line.getDatePromised() );
	}

	public FTUMProduction( MProjectLine line ) {
		super( line.getCtx(), 0, line.get_TrxName());
		MProject project = new MProject(line.getCtx(), line.getC_Project_ID(), line.get_TrxName());
		MWarehouse wh = new MWarehouse(line.getCtx(), project.getM_Warehouse_ID(), line.get_TrxName());
		
		MLocator M_Locator = null;
		int M_Locator_ID = 0;

		if (wh != null)
		{
			M_Locator = wh.getDefaultLocator();
			M_Locator_ID = M_Locator.getM_Locator_ID();
		}
		setAD_Client_ID(line.getAD_Client_ID());
		setAD_Org_ID(line.getAD_Org_ID());
		setM_Product_ID(line.getM_Product_ID());
		setProductionQty(line.getPlannedQty());
		setM_Locator_ID(M_Locator_ID);
		setDescription(project.getValue()+"_"+project.getName()+" Line: "+line.getLine()+" (project)");
		setC_Project_ID(line.getC_Project_ID());
		setC_BPartner_ID(project.getC_BPartner_ID());
		setC_Campaign_ID(project.getC_Campaign_ID());
		setAD_OrgTrx_ID(project.getAD_OrgTrx_ID());
		setC_Activity_ID(project.getC_Activity_ID());
		setC_ProjectPhase_ID(line.getC_ProjectPhase_ID());
		setC_ProjectTask_ID(line.getC_ProjectTask_ID());
		setMovementDate( Env.getContextAsDate(p_ctx, "#Date"));
	}

	
	@Override
	protected String isBom(int M_Product_ID) {
		String bom = DB.getSQLValueString(get_TrxName(), "SELECT isbom FROM M_Product WHERE M_Product_ID = ?", M_Product_ID);
		if ("N".compareTo(bom) == 0) {
			return "Attempt to create product line for Non Bill Of Materials";
		}
		int materials = DB.getSQLValue(get_TrxName(), "SELECT count(M_Product_BOM_ID) FROM M_Product_BOM WHERE M_Product_ID = ? AND (AD_Org_ID = 0 OR AD_Org_ID =?)", M_Product_ID, getAD_Org_ID());
		if (materials == 0)
		{
			return "Attempt to create product line for Bill Of Materials with no BOM Products";
		}
		return null;
	}
	
	@Override
	public String completeIt() {
		// Re-Check
		if (!m_justPrepared)
		{
			String status = prepareIt();
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		StringBuilder errors = new StringBuilder();
		int processed = 0;
		
		//IDEMPIERE-3107 Check if End Product in Production Lines exist
		if(!isHaveEndProduct(getLines())) {
			m_processMsg = "El proceso no tiene un producto final";
			return DocAction.STATUS_Invalid;
		}
		
		if(get_ValueAsBoolean("IsTransformation")){
			if(!verifyQty(getLines())){
				m_processMsg = "Las Cantidades a usadas no coinciden con las cantidades a transformar";
				return DocAction.STATUS_Invalid;
			}
			BigDecimal cost = (BigDecimal)get_Value("PriceActual");//get_ValueAsBigDecimal("PriceActual");
			BigDecimal invoicePrice = (BigDecimal)get_Value("AmountInvoiced");//get_ValueAsBigDecimal("AmountInvoiced");


			// id of transformation sequence 1001437

			String docNo = "";


			MSequence seq = new MSequence(getCtx(), 1001437, get_TrxName());
			docNo= MSequence.getDocumentNoFromSeq(seq, get_TrxName(), this);
			//set_ValueNoCheck 
			set_ValueOfColumn("DocumentNo",docNo);
			
			for(FTUMProductionLine line: getLines()){
				
					BigDecimal qtyUsed = ((BigDecimal)line.get_Value("qtyUsed")).setScale(4, BigDecimal.ROUND_UP);;//get_ValueAsBigDecimal("qtyUsed");
				if(qtyUsed.compareTo(BigDecimal.ZERO)!=0){	
					BigDecimal movementQty = ((BigDecimal)line.get_Value("movementQty"));//get_ValueAsBigDecimal("movementQty");
					BigDecimal lineInvoicePrice = invoicePrice.multiply(qtyUsed).setScale(4, BigDecimal.ROUND_UP);				 
					
					lineInvoicePrice = lineInvoicePrice.divide(movementQty,4, RoundingMode.HALF_UP);		 
					BigDecimal lineCost = cost.multiply(qtyUsed).setScale(4, BigDecimal.ROUND_UP);
					lineCost = lineCost.divide(movementQty,4, RoundingMode.HALF_UP);
					line.set_ValueOfColumn("PriceActual",lineCost);
					line.set_ValueOfColumn("PriceLastInv",lineInvoicePrice);
					line.saveEx(get_TrxName());
				}
				
			}
		}
			
		if (!isUseProductionPlan()) {
			FTUMProductionLine[] lines = getLines();
			errors.append(processLines(lines));
			if (errors.length() > 0) {
				m_processMsg = errors.toString();
				return DocAction.STATUS_Invalid;
			}
			processed = processed + lines.length;
		} else {
			Query planQuery = new Query(Env.getCtx(), I_M_ProductionPlan.Table_Name, "M_ProductionPlan.M_Production_ID=?", get_TrxName());
			List<MProductionPlan> plans = planQuery.setParameters(getM_Production_ID()).list();
			for(MProductionPlan plan : plans) {
				FTUMProductionLine[] lines = (FTUMProductionLine[]) plan.getLines();
				if (lines.length > 0) {
					errors.append(processLines(lines));
					if (errors.length() > 0) {
						m_processMsg = errors.toString();
						return DocAction.STATUS_Invalid;
					}
					processed = processed + lines.length;
				}


				plan.setProcessed(true);
				
				plan.saveEx();
			}
		}

		//		User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null)
		{
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		if(get_ValueAsBoolean("IsTransformation")){
			if(!updateProductPrices(getLines())){
				m_processMsg = "No se pudieron actualizar los precios de los productos hijos";
				return DocAction.STATUS_Invalid;
			}
		}
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		return DocAction.STATUS_Completed;
	}
	
	
	protected boolean updateProductPrices(FTUMProductionLine[] lines){
		
		String sql = "SELECT MAX(C_DocType_ID) FROM C_DocType WHERE DocBaseType = 'PCH'";
		
		int docTypeId = DB.getSQLValue(get_TrxName(), sql);
		
		for(FTUMProductionLine line : lines){
			if(line.isEndProduct() && line.getQtyUsed().compareTo(BigDecimal.ZERO)> 0){
				
				MProduct prod = new MProduct(getCtx(),line.getM_Product_ID(),get_TrxName());
				MBSCAPriceChange pc = new MBSCAPriceChange(getCtx(),0,get_TrxName());
				pc.setAD_Org_ID(getAD_Org_ID());
				pc.setAD_OrgTrx_ID(getAD_Org_ID());
				pc.setC_DocTypeTarget_ID(docTypeId);
				pc.setC_DocType_ID(0);
				pc.setDateAcct(new Timestamp(System.currentTimeMillis()));
				
				sql = "SELECT MAX(BSCA_ProductValue_ID) FROM BSCA_ProductValue WHERE Value='"+prod.getSKU()+"'";
				int BSCA_ProductValue_ID = DB.getSQLValue(get_TrxName(), sql);
				pc.setBSCA_ProductValue_ID(BSCA_ProductValue_ID);
				pc.setM_Product_ID(prod.get_ID());
				pc.setUser1W_ID(prod.get_ValueAsInt("User1W_ID"));
				pc.setUser1X_ID(prod.get_ValueAsInt("User1X_ID"));
				pc.setUser1Y_ID(prod.get_ValueAsInt("User1Y_ID"));
				pc.setUser1Z_ID(prod.get_ValueAsInt("User1Z_ID"));
				pc.setUser1_ID(prod.get_ValueAsInt("User1_ID"));
				
				BigDecimal amount = (BigDecimal)line.get_Value("PriceLastInv");
				
				pc.setPriceLastProduction(amount);
				pc.setPriceActual(amount);
				pc.setPriceListOld(BigDecimal.ONE);
				pc.setPriceStdOld(BigDecimal.ONE);
				pc.setPriceLimitOld(BigDecimal.ONE);
				pc.setPriceLastInv(BigDecimal.ZERO);
				pc.set_ValueOfColumn("BaseLimitPrice", amount); //from lastest version
				pc.set_ValueOfColumn("BaseListPrice", amount); //from lastest version
				pc.set_ValueOfColumn("BaseStdPrice", amount); //from lastest version
				
				
				pc.setM_Production_ID(get_ID());
				
				sql = "SELECT sl.BSCA_List_Discount FROM M_DiscountSchema  s "
						+ " INNER JOIN M_DiscountSchemaLine sl ON sl.m_discountschema_id = s.m_discountschema_id "
						+ " WHERE s.ad_client_id="+getAD_Client_ID()+" AND s.ad_org_id ="+getAD_Org_ID()+"  AND sl.m_product_id ="+prod.get_ID()+" AND s.isactive = 'Y' AND sl.isactive = 'Y'";
				
				BigDecimal percent = DB.getSQLValueBD(get_TrxName(), sql);
				if(percent==null){
					throw new AdempiereException("El porcentaje de no esta definido en el esquema de descuento para el producto:"+prod.getName()+", Cod:"+prod.getSKU()); 
				}
				if(percent.compareTo(BigDecimal.ZERO)> 0){
					pc.setPercentageProfitPLimit(percent);
					pc.setPercentageProfitPList(percent);
					pc.setPercentageProfitPStd(percent);
					percent = percent.divide(new BigDecimal(100));
					BigDecimal profit = amount.multiply(percent).setScale(2, RoundingMode.HALF_UP);
					pc.setBSCA_ProfitPriceLimit(profit);
					pc.setBSCA_ProfitPriceList(profit);
					pc.setBSCA_ProfitPriceStd(profit);
					pc.setBSCA_ProfitPriceLimitEntered(profit);
					pc.setBSCA_ProfitPriceListEntered(profit);
					pc.setBSCA_ProfitPriceStdEntered(profit);
					amount = amount.add(profit);
				}else{
					pc.setPercentageProfitPLimit(BigDecimal.ZERO);
					pc.setPercentageProfitPList(BigDecimal.ZERO);
					pc.setPercentageProfitPStd(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceLimit(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceList(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceStd(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceLimitEntered(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceListEntered(BigDecimal.ZERO);
					pc.setBSCA_ProfitPriceStdEntered(BigDecimal.ZERO);
				}
				
				pc.setPriceListEntered(amount);
				pc.setPriceLimitEntered(amount);
				pc.setPriceStdEntered(amount);
				pc.setPriceList(amount);
				pc.setPriceLimit(amount);
				pc.setPriceStd(amount);
				pc.setIsSetPriceList(true);
				
				sql = "select t.rate from C_Tax t "
						+ "inner join c_taxcategory tc on tc.c_taxcategory_id = t.c_taxcategory_id "
						+ "inner join M_Product p on p.c_taxcategory_id = tc.c_taxcategory_id"
						+ " where p.m_product_id = "+prod.get_ID()+" and t.issummary = 'N' and t.isactive = 'Y' and t.isdefault = 'Y' order by t.validfrom desc";
				
				percent = DB.getSQLValueBD(get_TrxName(), sql);
				
				
				if(percent.compareTo(BigDecimal.ZERO)> 0){
					percent = percent.divide(new BigDecimal(100));
					BigDecimal profit = amount.multiply(percent).setScale(2, RoundingMode.HALF_UP);
					pc.setTaxAmtPriceLimit(profit);
					pc.setTaxAmtPriceList(profit);
					pc.setTaxAmtPriceStd(profit);
					
					amount = amount.add(profit);
				}else{
					pc.setTaxAmtPriceLimit(BigDecimal.ZERO);
					pc.setTaxAmtPriceList(BigDecimal.ZERO);
					pc.setTaxAmtPriceStd(BigDecimal.ZERO);
				}
				
				pc.setPriceListWTax(amount);
				pc.setPriceLimitWTax(amount);
				pc.setPriceStdWTax(amount);
				
				pc.setDocStatus(pc.prepareIt());//MBSCAPriceChange.DOCSTATUS_Drafted);
				
				pc.saveEx(get_TrxName());
				
				System.out.println(pc.get_ID()+"-"+pc.getDocumentNo());
			
				
			}
			/*if(line.isEndProduct() && line.getQtyUsed().compareTo(BigDecimal.ZERO)> 0){
				MProduct prod = new MProduct(getCtx(),line.getM_Product_ID(),get_TrxName());
				MBSCAPriceChange pc = new MBSCAPriceChange(getCtx(),0,get_TrxName());
				pc.setAD_Org_ID(getAD_Org_ID());
				pc.setAD_OrgTrx_ID(getAD_Org_ID());
				pc.setC_DocTypeTarget_ID(docTypeId);
				pc.setC_DocType_ID(0);
				
				sql = "SELECT MAX(BSCA_ProductValue_ID) FROM BSCA_ProductValue WHERE Value='"+prod.getSKU()+"'";
				int BSCA_ProductValue_ID = DB.getSQLValue(get_TrxName(), sql);
				pc.setBSCA_ProductValue_ID(BSCA_ProductValue_ID);
				pc.setM_Product_ID(prod.get_ID());
				pc.setUser1W_ID(prod.get_ValueAsInt("User1W_ID"));
				pc.setUser1X_ID(prod.get_ValueAsInt("User1X_ID"));
				pc.setUser1Y_ID(prod.get_ValueAsInt("User1Y_ID"));
				pc.setUser1Z_ID(prod.get_ValueAsInt("User1Z_ID"));
				pc.setUser1_ID(prod.get_ValueAsInt("User1_ID"));
				
				BigDecimal amount = (BigDecimal)line.get_Value("PriceLastInv");
				pc.setPriceListEntered(amount);
				pc.setPriceLimitEntered(amount);
				pc.setPriceStdEntered(amount);
				pc.setIsSetPriceList(true);
				pc.setDocStatus(MBSCAPriceChange.DOCSTATUS_Drafted);
				pc.saveEx(get_TrxName());
				
				System.out.println(pc.get_ID()+"-"+pc.getDocumentNo());
			}		*/	
			
		}
		
		return true;
	}

	
	public FTUMProductionLine[] getLines() {
		ArrayList<FTUMProductionLine> list = new ArrayList<FTUMProductionLine>();
		
		String sql = "SELECT pl.M_ProductionLine_ID "
			+ "FROM M_ProductionLine pl "
			+ "WHERE pl.M_Production_ID = ?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement(sql, get_TrxName());
			pstmt.setInt(1, get_ID());
			rs = pstmt.executeQuery();
			while (rs.next())
				list.add( new FTUMProductionLine( getCtx(), rs.getInt(1), get_TrxName() ) );	
		}
		catch (SQLException ex)
		{
			throw new AdempiereException("Unable to load production lines", ex);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		
		FTUMProductionLine[] retValue = new FTUMProductionLine[list.size()];
		list.toArray(retValue);
		return retValue;
	}
	
	
	protected boolean verifyQty(MProductionLine[] lines){
		
		BigDecimal usedQty = new BigDecimal(0);
		BigDecimal requiredQty = getProductionQty();
		
		for(MProductionLine line : lines){
			
			if(line.isEndProduct() && line.isActive()){
				usedQty = usedQty.add(line.getQtyUsed());
			}
			
		}
		
		if(usedQty.compareTo(requiredQty) == 0){
			return true;
		}
		
		return false;
	}
	
	protected Object processLines(FTUMProductionLine[] lines) {
		StringBuilder errors = new StringBuilder();
		for ( int i = 0; i<lines.length; i++) {
			FTUMProductionLine line = new FTUMProductionLine(getCtx(), lines[i].getM_ProductionLine_ID(), get_TrxName());
			MWarehouse wh = (MWarehouse) line.getM_Locator().getM_Warehouse();
			String error = line.createTransactions(getMovementDate(), wh.isDisallowNegativeInv());
			if (!Util.isEmpty(error)) {
				errors.append(error);
			} else { 
				line.setProcessed( true );
				line.saveEx(get_TrxName());
			}
		}

		return errors.toString();
	}


	protected boolean isHaveEndProduct(MProductionLine[] lines) {
		
		for(MProductionLine line : lines) {
			if(line.isEndProduct())
				return true;			
		}
		return false;
	}
	@Override
	public String prepareIt() {
		if (log.isLoggable(Level.INFO)) log.info(toString());
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;
		log.warning("transformation");
		//	Std Period open?
		MPeriod.testPeriodOpen(getCtx(), getMovementDate(), MDocType.DOCBASETYPE_MaterialProduction, getAD_Org_ID());

		if ( getIsCreated().equals("N") )
		{
			//m_processMsg = "Not created";
			if(get_ValueAsBoolean("IsTransformation")){
				
			m_processMsg = "Debe crear las lineas o actualizarlas(Recrearlas) en caso de haber cambiado la cantidad a transformar";
			return DocAction.STATUS_Invalid; 
			}else {
			m_processMsg = "Debe crear las lineas o actualizarlas(Recrearlas) en caso de haber cambiado la cantidad a producir";
			return DocAction.STATUS_Invalid; 
			}
		}
		if(get_ValueAsBoolean("IsTransformation")){
			String sql = "SELECT COALESCE(SUM(QtyAvailable),0) FROM FTU_RV_Storage_Available_Product  WHERE M_Product_ID = "+getM_Product_ID()+" AND AD_Org_ID = "+getAD_Org_ID()+" AND M_Locator_ID="+getM_Locator_ID();
			BigDecimal qty = DB.getSQLValueBD(get_TrxName(), sql);
			if(qty!=null){
				if(qty.compareTo(getProductionQty())<0){
					m_processMsg = "La cantidad a transformar es menor a la cantidad disponible en el almacen y la ubicacion seleccionada";
					return DocAction.STATUS_Invalid; 
				}
			}
		}
		
		if (!isUseProductionPlan()) {
			m_processMsg = validateEndProduct(getM_Product_ID());			
			if (!Util.isEmpty(m_processMsg)) {
				return DocAction.STATUS_Invalid;
			}
		} else {
			Query planQuery = new Query(getCtx(), I_M_ProductionPlan.Table_Name, "M_ProductionPlan.M_Production_ID=?", get_TrxName());
			List<MProductionPlan> plans = planQuery.setParameters(getM_Production_ID()).list();
			for(MProductionPlan plan : plans) {
				m_processMsg = validateEndProduct(plan.getM_Product_ID());
				if (!Util.isEmpty(m_processMsg)) {
					return DocAction.STATUS_Invalid;
				}
			}
		}

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		return DocAction.STATUS_InProgress;
	}
	
	@Override
	protected String validateEndProduct(int M_Product_ID) {
		String msg = isBom(M_Product_ID);
		if (!Util.isEmpty(msg))
			return msg;

		if (!costsOK(M_Product_ID)) {
			msg = "Excessive difference in standard costs";
			if (MSysConfig.getBooleanValue(MSysConfig.MFG_ValidateCostsDifferenceOnCreate, false, getAD_Client_ID())) {
				return msg;
			} else {
				log.warning(msg);
			}
		}

		return null;
	}
	
	@Override
	public boolean processIt(String processAction) {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (processAction, getDocAction());
	}
	
	@Override
	public boolean reverseAccrualIt() {
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		FTUMProduction reversal = reverse(true);
		if (reversal == null)
			return false;

		// After reverseAccrual
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSEACCRUAL);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();

		return true;
	}

	
	@Override
	public boolean reverseCorrectIt() 
	{
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		FTUMProduction reversal = reverse(false);
		if (reversal == null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		m_processMsg = reversal.getDocumentNo();

		return true;
	}

	private FTUMProduction reverse(boolean accrual) {
		Timestamp reversalDate = accrual ? Env.getContextAsDate(getCtx(), "#Date") : getMovementDate();
		if (reversalDate == null) {
			reversalDate = new Timestamp(System.currentTimeMillis());
		}

		MPeriod.testPeriodOpen(getCtx(), reversalDate, Doc.DOCTYPE_MatProduction, getAD_Org_ID());
		FTUMProduction reversal = null;
		reversal = copyFrom (reversalDate);

		StringBuilder msgadd = new StringBuilder("{->").append(getDocumentNo()).append(")");
		reversal.addDescription(msgadd.toString());
		reversal.setReversal_ID(getM_Production_ID());
		reversal.saveEx(get_TrxName());

		if (!reversal.processIt(DocAction.ACTION_Complete))
		{
			m_processMsg = "Reversal ERROR: " + reversal.getProcessMsg();
			return null;
		}

		reversal.closeIt();
		reversal.setProcessing (false);
		reversal.setDocStatus(DOCSTATUS_Reversed);
		reversal.setDocAction(DOCACTION_None);
		reversal.saveEx(get_TrxName());

		msgadd = new StringBuilder("(").append(reversal.getDocumentNo()).append("<-)");
		addDescription(msgadd.toString());

		setProcessed(true);
		setReversal_ID(reversal.getM_Production_ID());
		setDocStatus(DOCSTATUS_Reversed);	//	may come from void
		setDocAction(DOCACTION_None);		

		return reversal;
	}

	private FTUMProduction copyFrom(Timestamp reversalDate) {
		FTUMProduction to = new FTUMProduction(getCtx(), 0, get_TrxName());
		PO.copyValues (this, to, getAD_Client_ID(), getAD_Org_ID());

		to.set_ValueNoCheck ("DocumentNo", null);
		//
		to.setDocStatus (DOCSTATUS_Drafted);		//	Draft
		to.setDocAction(DOCACTION_Complete);
		to.setMovementDate(reversalDate);
		to.setIsComplete(false);
		to.setIsCreated("Y");
		to.setProcessing(false);
		to.setProcessed(false);
		to.setIsUseProductionPlan(isUseProductionPlan());
		if (isUseProductionPlan()) {
			to.saveEx();
			Query planQuery = new Query(Env.getCtx(), I_M_ProductionPlan.Table_Name, "M_ProductionPlan.M_Production_ID=?", get_TrxName());
			List<MProductionPlan> fplans = planQuery.setParameters(getM_Production_ID()).list();
			for(MProductionPlan fplan : fplans) {
				MProductionPlan tplan = new MProductionPlan(getCtx(), 0, get_TrxName());
				PO.copyValues (fplan, tplan, getAD_Client_ID(), getAD_Org_ID());
				tplan.setM_Production_ID(to.getM_Production_ID());
				tplan.setProductionQty(fplan.getProductionQty().negate());
				tplan.setProcessed(false);
				tplan.saveEx();

				MProductionLine[] flines = fplan.getLines();
				for(MProductionLine fline : flines) {
					MProductionLine tline = new MProductionLine(tplan);
					PO.copyValues (fline, tline, getAD_Client_ID(), getAD_Org_ID());
					tline.setM_ProductionPlan_ID(tplan.getM_ProductionPlan_ID());
					tline.setMovementQty(fline.getMovementQty().negate());
					tline.setPlannedQty(fline.getPlannedQty().negate());
					tline.setQtyUsed(fline.getQtyUsed().negate());
					tline.saveEx();
				}
			}
		} else {
			to.setProductionQty(getProductionQty().negate());	
			to.saveEx();
			FTUMProductionLine[] flines = getLines();
			for(FTUMProductionLine fline : flines) {
				FTUMProductionLine tline = new FTUMProductionLine(to);
				PO.copyValues (fline, tline, getAD_Client_ID(), getAD_Org_ID());
				tline.setM_Production_ID(to.getM_Production_ID());
				tline.setMovementQty(fline.getMovementQty().negate());
				tline.setPlannedQty(fline.getPlannedQty().negate());
				tline.setQtyUsed(fline.getQtyUsed().negate());
				tline.saveEx();
			}
		}

		return to;
	}

		
	@Override
	
	protected boolean beforeSave(boolean newRecord) {
	
		if (getM_Product_ID() > 0) {
		
			if (isUseProductionPlan()) {
				setIsUseProductionPlan(false);
			}
		} else { 
			if (!isUseProductionPlan()) {
				setIsUseProductionPlan(true);
			}
		}
		return true;	
	}
	
	
}
