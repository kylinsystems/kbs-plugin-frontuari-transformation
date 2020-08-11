package com.bucaresystems.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.compiere.model.MConversionRate;
import org.compiere.model.MCurrency;
import org.compiere.model.MDiscountSchema;
import org.compiere.model.MDiscountSchemaLine;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MMovement;
import org.compiere.model.MMovementConfirm;
import org.compiere.model.MMovementLine;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrg;
import org.compiere.model.MPInstance;
import org.compiere.model.MPriceList;
import org.compiere.model.MPriceListVersion;
import org.compiere.model.MProcess;
import org.compiere.model.MProcessPara;
import org.compiere.model.MProduct;
import org.compiere.model.MProductPrice;
import org.compiere.model.MProduction;
import org.compiere.model.MStorageOnHand;
import org.compiere.model.MSysConfig;
import org.compiere.model.MTax;
import org.compiere.model.MWarehouse;
import org.compiere.model.ModelValidationEngine;
import org.compiere.model.ModelValidator;
import org.compiere.model.Query;
import org.compiere.process.DocAction;
import org.compiere.process.DocOptions;
import org.compiere.process.DocumentEngine;
import org.compiere.process.ProcessInfo;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;

//import com.bucaresystems.process.BSCA_SendTxtScale;

/**
 * Model para Documento de Control de Cambio de Precio
 * @author Ing. Victor Suarez <victor.suarez.is@gmail.com>; <vsuarez@bucaresystems.com>
 */
public class MBSCAPriceChange extends X_BSCA_PriceChange implements DocAction, DocOptions {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755773903864584477L;
	
	/**	Process Message 			*/
	private String		m_processMsg = null;
	/** Summary						*/
	private String m_summary = null;
	/**	Just Prepared Flag			*/
	private boolean		m_justPrepared = false;
	/** Prices						*/
	private BigDecimal priceListEntered = Env.ZERO;
	private BigDecimal priceStdEntered = Env.ZERO;
	private BigDecimal priceLimitEntered = Env.ZERO;
//	private BigDecimal priceLastInvoice = Env.ZERO;
	private BigDecimal priceLastOrder = Env.ZERO;
	private BigDecimal priceList = Env.ZERO;
	private BigDecimal priceStd = Env.ZERO;
	private BigDecimal priceLimit = Env.ZERO;
	/**	Taxes Amt					*/
	private BigDecimal taxAmtPriceList = Env.ZERO;
	private BigDecimal taxAmtPriceLimit = Env.ZERO;
	private BigDecimal taxAmtPriceStd = Env.ZERO;
	/** Prices With Taxes Amt		*/
	private BigDecimal priceListWTax = Env.ZERO;
	private BigDecimal priceStdWTax = Env.ZERO;
	private BigDecimal priceLimitWTax = Env.ZERO;
	/**	Profit						*/
	private BigDecimal profitPriceListEntered = Env.ZERO;
	private BigDecimal profitPriceStdEntered = Env.ZERO;
	private BigDecimal profitPriceLimitEntered = Env.ZERO;
	private BigDecimal percentageProfitPList = Env.ZERO;
	private BigDecimal percentageProfitPStd = Env.ZERO;
	private BigDecimal percentageProfitPLimit = Env.ZERO;
	private BigDecimal profitPriceList = Env.ZERO;
	private BigDecimal profitPriceStd = Env.ZERO;
	private BigDecimal profitPriceLimit = Env.ZERO;
	/**	Last Invoice				*/
	private int C_Invoice_ID = 0;
	private Timestamp dateInvoiced = null;
	MInvoiceLine invoiceLine = null;
	/** Last Order					*/
	private int C_Order_ID = 0;
	private Timestamp dateOrdered = null;
	/**	Product						*/
	public int M_Product_ID = 0;
	/**	Price Base				*/
	public BigDecimal priceBase = BigDecimal.ZERO;
	
	public StringBuilder info = new StringBuilder();


	public MBSCAPriceChange(Properties ctx, int BSCA_PriceChange_ID,
			String trxName) {
		super(ctx, BSCA_PriceChange_ID, trxName);
	}

	public MBSCAPriceChange(Properties ctx, ResultSet rs, String trxName) {
		super(ctx, rs, trxName);
	}
	
	public MBSCAPriceChange(Properties ctx, X_BSCA_ProductValue productValue, MInOut inOut, String trxName) {
		super(ctx, 0, trxName);
		setClientOrg(inOut);
		if(productValue != null) {
			setBSCA_ProductValue_ID(productValue.get_ID());
			setM_Product_ID(productValue.getM_Product_ID());
		}
		setDateAcct(inOut.getDateAcct());
		setM_InOut_ID(inOut.getM_InOut_ID());
		
		setPriceActual(Env.ZERO);
		setPriceList(Env.ZERO);
		setPriceStd(Env.ZERO);
		setPriceLimit(Env.ZERO);
		setPriceListEntered(Env.ZERO);
		setPriceStdEntered(Env.ZERO);
		setPriceLimitEntered(Env.ZERO);
		setPriceListOld(Env.ZERO);
		setPriceStdOld(Env.ZERO);
		setPriceLimitOld(Env.ZERO);
	}
	
	public MBSCAPriceChange(Properties ctx, int product_ID, MInOut inOut, MMovementConfirm confirm, int AD_Org_ID, int AD_OrgTrx_ID, String trxName) {
		super(ctx, 0, trxName);
		setM_Product_ID(product_ID);
		if(product_ID > 0) {
//			String sqlBOM = "SELECT M_Product_ID FROM M_Product_BOM WHERE M_ProductBOM_ID = ? ";
//			M_Product_ID = DB.getSQLValue(get_TrxName(), sqlBOM, product_ID);
			if(M_Product_ID <= 0)
				M_Product_ID = product_ID;
		}
		setDateAcct(Env.getContextAsDate(getCtx(), "#Date"));
		if(inOut != null) {
			setClientOrg(inOut);
			setM_InOut_ID(inOut.getM_InOut_ID());
			if(AD_OrgTrx_ID <=0)
				setAD_OrgTrx_ID(inOut.getAD_Org_ID());
			else
				setAD_OrgTrx_ID(AD_OrgTrx_ID);
			if(AD_Org_ID <=0)
				AD_Org_ID = inOut.getAD_Org_ID();
		} else if(confirm != null){
			setClientOrg(confirm);
			setM_MovementConfirm_ID(confirm.getM_MovementConfirm_ID());
			MMovement movement = (MMovement) confirm.getM_Movement();
			setAD_OrgTrx_ID(movement.getAD_Org_ID());
			if(AD_Org_ID <=0)
				AD_Org_ID = confirm.getAD_Org_ID();
		} else {
			setAD_OrgTrx_ID(AD_OrgTrx_ID);
		}
		setAD_Org_ID(AD_Org_ID);
		if(AD_OrgTrx_ID <= 0)
			setAD_OrgTrx_ID(AD_Org_ID);
		setPriceActual(Env.ZERO);
		setPriceList(Env.ZERO);
		setPriceStd(Env.ZERO);
		setPriceLimit(Env.ZERO);
		setPriceListEntered(Env.ZERO);
		setPriceStdEntered(Env.ZERO);
		setPriceLimitEntered(Env.ZERO);
		setPriceListOld(Env.ZERO);
		setPriceStdOld(Env.ZERO);
		setPriceLimitOld(Env.ZERO);
	}
	
	public void setLastInvoice() {
		MDiscountSchema discountSchema = new Query(getCtx(), MDiscountSchema.Table_Name, "AD_Org_ID=? AND ValidFrom <= ?", null)
			.setOnlyActiveRecords(true).setParameters(getAD_OrgTrx_ID(), getDateAcct()).first();
		MDiscountSchemaLine discountSchemaLine = null;
		if(discountSchema != null) {
			discountSchemaLine = new Query(getCtx(), MDiscountSchemaLine.Table_Name, "M_DiscountSchema_ID =? AND M_Product_ID =?", get_TrxName())
				.setOnlyActiveRecords(true).setParameters(discountSchema.getM_DiscountSchema_ID(), M_Product_ID).first();
		}
		if(discountSchemaLine != null && "H".equals(discountSchemaLine.getList_Base())) {
			// Higher Invoice Price
			MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
			String whereOrg = "AD_Org_ID = " + getAD_OrgTrx_ID();
			String sqlHPOP = "SELECT BSCA_HigherPOPrice FROM AD_OrgInfo WHERE AD_Org_ID = ? ";
			String higherPOPrice = DB.getSQLValueString(null, sqlHPOP, org.getAD_Org_ID()); 
			if("T".equals(higherPOPrice)) {
				String sqlPOrg = "SELECT Parent_Org_ID FROM AD_OrgInfo WHERE AD_Org_ID = ? ";
				int parent_Org_ID = DB.getSQLValue(null, sqlPOrg, org.getAD_Org_ID());
				whereOrg = "AD_Org_ID IN (SELECT AD_Org_ID FROM AD_OrgInfo WHERE Parent_Org_ID = " + parent_Org_ID + ")";
			}
			invoiceLine = new Query(getCtx(), MInvoiceLine.Table_Name, "C_Invoice.IsSOTrx = 'N' AND C_Invoice.DocStatus = 'CO' AND C_DocType.DocBaseType = 'API' AND C_Invoice." + whereOrg
					+ " AND C_InvoiceLine.M_Product_ID = ? AND NOT EXISTS (SELECT M_Product_ID FROM C_InvoiceLine il "
					+ " JOIN C_Invoice i ON i.C_Invoice_ID = il.C_Invoice_ID JOIN C_DocType doc ON doc.C_DocType_ID = i.C_DocType_ID "
					+ " WHERE "
					+ " i.DocStatus = 'CO' AND doc.DocBaseType = 'APC' AND i.LVE_InvoiceAffected_ID = C_Invoice.C_Invoice_ID AND il.QtyInvoiced = C_InvoiceLine.QtyInvoiced AND il.M_Product_ID =?)", null)
				.addJoinClause("JOIN C_Invoice C_Invoice ON C_InvoiceLine.C_Invoice_ID = C_Invoice.C_Invoice_ID")
				.addJoinClause("JOIN C_DocType C_DocType ON C_Invoice.C_DocType_ID = C_DocType.C_DocType_ID")
				.setParameters(M_Product_ID, M_Product_ID).first();
			
			// Higher Production Price
			MProduction production = null; 
			BigDecimal priceProduction = BigDecimal.ZERO;
			if(getM_Product() != null) {
				MProduct product = (MProduct) getM_Product();
				if(product.isBOM() && "P".equalsIgnoreCase(product.get_ValueAsString("Modality"))) {
					production = new Query(getCtx(), MProduction.Table_Name, "Modality = 'P' AND " + whereOrg + " AND DocStatus = 'CO' AND PriceActual > 0 AND M_Product_ID = ?", get_TrxName())
						.setOnlyActiveRecords(true).setOrderBy("PriceActual DESC")
						.setParameters(product.getM_Product_ID()).first();
					if(production.get_Value("PriceActual") != null)
						priceProduction = (BigDecimal) production.get_Value("PriceActual");
				}
			}
			
			if(invoiceLine != null && production != null) {
				if(priceProduction.compareTo(invoiceLine.getPriceActual()) > 0)
					invoiceLine = null;
				else
					production = null;
			}
			if(invoiceLine != null) {
				priceBase = invoiceLine.getPriceActual();
				C_Invoice_ID = invoiceLine.getC_Invoice_ID();
				dateInvoiced = invoiceLine.getC_Invoice().getDateAcct();
				setC_Invoice_ID(C_Invoice_ID);
				setDateInvoiced(dateInvoiced);
				setPriceLastInv(priceBase);
				if("T".equals(higherPOPrice))
					setAD_OrgTrx_ID(invoiceLine.getAD_Org_ID());
			} else if(production != null) {
				setM_Production_ID(production.getM_Production_ID());
				setMovementDate(production.getMovementDate());
				priceBase = priceProduction;
				setPriceLastProduction(priceBase);
				if("T".equals(higherPOPrice))
					setAD_OrgTrx_ID(production.getAD_Org_ID());;
			}
		} else {
			// Price Last Invoice
			invoiceLine = new Query(getCtx(), MInvoiceLine.Table_Name, "C_Invoice.IsSOTrx = 'N' AND C_Invoice.DocStatus = 'CO' AND C_DocType.DocBaseType = 'API' AND C_Invoice.AD_Org_ID = ? "
					+ "AND C_InvoiceLine.M_Product_ID = ? AND NOT EXISTS (SELECT M_Product_ID FROM C_InvoiceLine il "
					+ "JOIN C_Invoice i ON i.C_Invoice_ID = il.C_Invoice_ID JOIN C_DocType doc ON doc.C_DocType_ID = i.C_DocType_ID "
					+ "WHERE "
					+ "i.DocStatus = 'CO' AND doc.DocBaseType = 'APC' AND i.LVE_InvoiceAffected_ID = C_Invoice.C_Invoice_ID AND il.QtyInvoiced = C_InvoiceLine.QtyInvoiced AND il.M_Product_ID =?)", null)
				.addJoinClause("JOIN C_Invoice C_Invoice ON C_InvoiceLine.C_Invoice_ID = C_Invoice.C_Invoice_ID")
				.addJoinClause("JOIN C_DocType C_DocType ON C_Invoice.C_DocType_ID = C_DocType.C_DocType_ID")
				.setOnlyActiveRecords(true).setOrderBy("C_Invoice.DateAcct DESC, C_Invoice.ProcessedOn DESC")
				.setParameters(getAD_OrgTrx_ID(), M_Product_ID, M_Product_ID).first();
			
			// Price Last Production
			MProduction production = null; 
			if(getM_Product() != null) {
				MProduct product = (MProduct) getM_Product();
				if(product.isBOM() && "P".equalsIgnoreCase(product.get_ValueAsString("Modality"))) {
					production = new Query(getCtx(), MProduction.Table_Name, "Modality = 'P' AND AD_Org_ID = ? AND DocStatus = 'CO' AND PriceActual > 0 AND M_Product_ID = ?", get_TrxName())
						.setOnlyActiveRecords(true).setOrderBy("MovementDate DESC, ProcessedOn DESC")
						.setParameters(getAD_OrgTrx_ID(), product.getM_Product_ID()).first();
				}
			}
			
			if(invoiceLine != null && production != null) {
				if(production.getProcessedOn().compareTo(invoiceLine.getC_Invoice().getProcessedOn()) > 0)
					invoiceLine = null;
				else
					production = null;
			}
			if(invoiceLine != null) {
				priceBase = invoiceLine.getPriceActual();
				C_Invoice_ID = invoiceLine.getC_Invoice_ID();
				dateInvoiced = invoiceLine.getC_Invoice().getDateAcct();
				setC_Invoice_ID(C_Invoice_ID);
				setDateInvoiced(dateInvoiced);
				setPriceLastInv(priceBase);
			} else if(production != null) {
				setM_Production_ID(production.getM_Production_ID());
				setMovementDate(production.getMovementDate());
				if(production.get_Value("PriceActual") != null)
					priceBase = (BigDecimal) production.get_Value("PriceActual");
				setPriceLastProduction(priceBase);
			}
		}
	}
	
	public void setLastInvoice(X_BSCA_PC_MultiOrgs pcOrg) {
		if(pcOrg.getM_Product_ID() > 0 && pcOrg.getM_Product_ID() != getM_Product_ID()) {
			if(pcOrg.getPurchasePriceEntered() == null || pcOrg.getPurchasePriceEntered().compareTo(BigDecimal.ZERO) <= 0)
				setLastInvoice();
			else {
				priceBase = pcOrg.getPurchasePriceEntered();
				setPriceLastInv(priceBase);
			}
		} else {
			if(pcOrg.isSamePurchasePrice() || 
					(!pcOrg.isSamePurchasePrice() && (pcOrg.getPurchasePriceEntered() == null || 
										pcOrg.getPurchasePriceEntered().compareTo(BigDecimal.ZERO) <= 0))) {
				MBSCAPriceChange pc = (MBSCAPriceChange) pcOrg.getBSCA_PriceChange();
				if(pc.getPriceLastInv() != null && pc.getPriceLastInv().compareTo(BigDecimal.ZERO) > 0) {
					priceBase = pc.getPriceLastInv(); 
					setC_Invoice_ID(pc.getC_Invoice_ID());
					setDateInvoiced(pc.getDateInvoiced());
					setPriceLastInv(pc.getPriceLastInv());
				} else if(pc.getPriceLastProduction() != null && pc.getPriceLastProduction().compareTo(BigDecimal.ZERO) > 0) {
					priceBase = pc.getPriceLastProduction();
					setM_Production_ID(pc.getM_Production_ID());
					setMovementDate(pc.getMovementDate());
					setPriceLastProduction(pc.getPriceLastProduction());
				}
			} else {
				priceBase = pcOrg.getPurchasePriceEntered();
				setPriceLastInv(priceBase);
			}
		}
	}

	public void setLastOrder() {
		// Price Last Order
		MOrderLine orderLine = new Query(getCtx(), MOrderLine.Table_Name, "C_Order.IsSOTrx = 'N' AND C_Order.DocStatus = 'CO' AND C_Order.AD_Org_ID = ? AND C_OrderLine.M_Product_ID = ?", null)
			.addJoinClause("JOIN C_Order C_Order ON C_OrderLine.C_Order_ID = C_Order.C_Order_ID")
			.setOnlyActiveRecords(true).setOrderBy("C_Order.DateAcct DESC")
			.setParameters(getAD_OrgTrx_ID(), M_Product_ID).first();

		if(orderLine != null) {
//			MProduct product = new MProduct(getCtx(), M_Product_ID, get_TrxName());
//			if(product.isBOM() && "T".equalsIgnoreCase(product.get_ValueAsString("Modality"))) {
//				Query query = new Query(getCtx(), MProductBOM.Table_Name, "IsProductShrinkage = 'N' AND AD_Client_ID =? AND M_Product_ID=? AND AD_Org_ID =?", get_TrxName())
//						.setOnlyActiveRecords(true);
//				List<MProductBOM> BOMs = query.setParameters(getAD_Client_ID(), product.getM_Product_ID(), getAD_OrgTrx_ID()).list();
//				if(BOMs.isEmpty())
//					BOMs = query.setParameters(getAD_Client_ID(), product.getM_Product_ID(), 0).list();
//				BigDecimal totalBom = BigDecimal.ZERO;
//				for(MProductBOM BOM : BOMs) {
//					totalBom = totalBom.add(BOM.getBOMQty());
//				}
//				if(totalBom.compareTo(BigDecimal.ZERO) > 0)
//					priceLastOrder = orderLine.getPriceActual().divide(totalBom, orderLine.getC_Order().getC_Currency().getStdPrecision(), BigDecimal.ROUND_HALF_UP);
//				else
//					priceLastOrder = orderLine.getPriceActual();
//			} else
				priceLastOrder = orderLine.getPriceActual();
			C_Order_ID = orderLine.getC_Order_ID();
			dateOrdered = orderLine.getC_Order().getDateAcct();
		}
		setC_Order_ID(C_Order_ID);
		setDateOrdered(dateOrdered);
		setPriceLastOrd(priceLastOrder);
	}
	
	public String setValues() {
		String result = null;
		MProductPrice productPrice = new Query(getCtx(), MProductPrice.Table_Name, "M_ProductPrice.M_Product_ID=? AND M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault='Y' AND M_PriceList.AD_Org_ID=?", null)
			.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_ProductPrice.M_PriceList_Version_ID = M_PriceList_Version.M_PriceList_Version_ID")
			.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
			.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
			.setParameters(M_Product_ID, getAD_OrgTrx_ID()).first();

		if(productPrice != null)
			setPriceActual(productPrice.getPriceList());
		else {
		//	MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_ProductNotPriceList"));
		//	Object[] arguments = new Object[]{product.getName(), org.getName()};
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_BSCA_ProductValue_ID, null);
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_M_Product_ID, null);
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_AD_OrgTrx_ID, null);
		//	mTab.dataRefresh();
		//	return mf.format(arguments);
			setPriceActual(Env.ZERO);
		}

		MPriceListVersion listVersion = null;
		if(getM_PriceList_Version_ID() != 0)
			listVersion = new MPriceListVersion(getCtx(), getM_PriceList_Version_ID(), get_TrxName());
		
		percentageProfitPList = getPercentageProfitPList();
		MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
		MDiscountSchema discountSchema = new Query(getCtx(), MDiscountSchema.Table_Name, "AD_Org_ID=? AND ValidFrom <= ?", null)
			.setOnlyActiveRecords(true).setParameters(getAD_Org_ID(), getDateAcct()).first();
		if(discountSchema == null) {
			MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_NotPriceListForOrg"));
			Object[] arguments = new Object[]{org.getName()};
			m_processMsg = mf.format(arguments);
			return m_processMsg;
		}
		MDiscountSchemaLine discountSchemaLine = new Query(getCtx(), MDiscountSchemaLine.Table_Name, "M_DiscountSchema_ID =? AND M_Product_ID =?", get_TrxName())
					.setOnlyActiveRecords(true).setParameters(discountSchema.getM_DiscountSchema_ID(), M_Product_ID).first();
		if(discountSchemaLine != null) {
			// Set Prices Type Bases
			if(getList_Base() == null || "".equals(getList_Base()))
				setList_Base(discountSchemaLine.getList_Base());
			setStd_Base(discountSchemaLine.getStd_Base());
			setLimit_Base(discountSchemaLine.getLimit_Base());
		} else if(percentageProfitPList == null || percentageProfitPList.compareTo(BigDecimal.ZERO) == 0 || getList_Base() == null || getList_Base() == "") {
			result = "Producto " + getM_Product().getName() + " no tiene Esquema de Precio. Para la Organizacion: " + org.getName();
		}
			
		// Calc Prices
		priceListEntered = calcPrice(getList_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_List_Base);
		priceStdEntered = calcPrice(getStd_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_Std_Base);
		priceLimitEntered = calcPrice(getLimit_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_Limit_Base);
		
		if(percentageProfitPList.compareTo(BigDecimal.ZERO) > 0 && priceBase.compareTo(BigDecimal.ZERO) > 0)
			priceListEntered = calcProfitFromPercentage(priceBase, percentageProfitPList, getC_Invoice().getC_Currency().getStdPrecision());
		
    	priceList = priceListEntered;
    	priceStd = priceStdEntered;
    	priceLimit = priceLimitEntered;
    	
		boolean isTaxIncluded = false;
		int scaleTax = 2;
		if(productPrice != null) {
			MPriceList SOPriceList = (MPriceList) productPrice.getM_PriceList_Version().getM_PriceList();
			isTaxIncluded = SOPriceList.isTaxIncluded();
			scaleTax = SOPriceList.getStandardPrecision();
		}
		
		MTax tax = new Query(getCtx(), MTax.Table_Name, "C_TaxCategory_ID=? AND IsDefault='Y' AND SOPOType!='P'", null)
			.setOnlyActiveRecords(true).setOrderBy("ValidFrom DESC").setParameters(getM_Product().getC_TaxCategory_ID()).first();
		if(tax != null) {
			taxAmtPriceList = tax.calculateTax(priceListEntered, isTaxIncluded, scaleTax);
			taxAmtPriceStd = tax.calculateTax(priceStdEntered, isTaxIncluded, scaleTax);
			taxAmtPriceLimit = tax.calculateTax(priceLimitEntered, isTaxIncluded, scaleTax);
			if(isTaxIncluded) {
				priceListWTax = priceListEntered;
				priceStdWTax = priceStdEntered;
				priceLimitWTax = priceLimitEntered;
			} else {
				priceListWTax = priceListEntered.add(taxAmtPriceList);
				priceStdWTax = priceStdEntered.add(taxAmtPriceStd);
				priceLimitWTax = priceLimitEntered.add(taxAmtPriceLimit);
			}
		} else
			m_processMsg = Msg.getMsg(getCtx(), "BSCA_NotTaxFound");
		
		// Set Price With Tax - Precios con Impuesto
		setPriceListWTax(priceListWTax);
		setPriceStdWTax(priceStdWTax);
		setPriceLimitWTax(priceLimitWTax);

		// Set Tax - Impuestos
		setTaxAmtPriceList(taxAmtPriceList);
		setTaxAmtPriceStd(taxAmtPriceStd);
		setTaxAmtPriceLimit(taxAmtPriceLimit);
		
		// Calc Profit Prices - Calculos de Ganancias de Precios
		profitPriceListEntered = calcProfit(priceListEntered, priceBase);
		profitPriceStdEntered = calcProfit(priceStdEntered, priceBase);
		profitPriceLimitEntered = calcProfit(priceLimitEntered, priceBase);
		
		// Calc Profit Percentages - Calculos de Porcentajes de Precios
		if(percentageProfitPList.compareTo(BigDecimal.ZERO) == 0)
			percentageProfitPList = calcPercentageProfit(priceListEntered, profitPriceListEntered);
    	percentageProfitPStd = calcPercentageProfit(priceStdEntered, profitPriceStdEntered);
    	percentageProfitPLimit = calcPercentageProfit(priceLimitEntered, profitPriceLimitEntered);
    	
    	// Calc Profit - Calculo de Ganancias
    	profitPriceList = calcProfit(priceList, priceBase);
    	profitPriceStd = calcProfit(priceStd, priceBase);
    	profitPriceLimit = calcProfit(priceLimit, priceBase);
    	
    	// Set Profit Prices Entered - Ganancias Ingresadas
    	setBSCA_ProfitPriceListEntered(profitPriceListEntered);
    	setBSCA_ProfitPriceStdEntered(profitPriceStdEntered);
    	setBSCA_ProfitPriceLimitEntered(profitPriceLimitEntered);
    	 
    	// Set Profit Percentage - Porcentajes de Ganancias Enteradas
    	setPercentageProfitPList(percentageProfitPList);
    	setPercentageProfitPStd(percentageProfitPStd);
    	setPercentageProfitPLimit(percentageProfitPLimit);
    	
    	// Set Profit Prices - Ganancias
    	setBSCA_ProfitPriceList(profitPriceList);
    	setBSCA_ProfitPriceStd(profitPriceStd);
    	setBSCA_ProfitPriceLimit(profitPriceLimit);
    	
    	// Set Prices - Precios
    	setPriceList(priceList);
    	setPriceStd(priceStd);
    	setPriceLimit(priceLimit);
    	
    	// Set Prices Entered - Precios Ingresados 
    	setPriceListEntered(priceListEntered);
    	setPriceStdEntered(priceStdEntered);
    	setPriceLimitEntered(priceLimitEntered);
    	return result;
	}
	
	public String setValues(X_BSCA_PC_MultiOrgs pcOrg) {
		if(pcOrg.getM_Product_ID() > 0 && pcOrg.getM_Product_ID() != getM_Product_ID()) {
			if(pcOrg.getSalePriceEntered() == null || pcOrg.getSalePriceEntered().compareTo(BigDecimal.ZERO) <= 0)
				return setValues();
			else 
				return setValues(pcOrg.getSalePriceEntered());
		} else {
			if(!pcOrg.isSameSalePrice() && pcOrg.getSalePriceEntered() != null && pcOrg.getSalePriceEntered().compareTo(BigDecimal.ZERO) > 0)
				return setValues(pcOrg.getSalePriceEntered());
			else {
				MBSCAPriceChange pc = (MBSCAPriceChange) pcOrg.getBSCA_PriceChange();
				setPriceActual(pc.getPriceActual());
				setList_Base(pc.getList_Base());
				setStd_Base(pc.getStd_Base());
				setLimit_Base(pc.getLimit_Base());
				
				// Set Price With Tax - Precios con Impuesto
				setPriceListWTax(pc.getPriceListWTax());
				setPriceStdWTax(pc.getPriceStd());
				setPriceLimitWTax(pc.getPriceLimitWTax());

				// Set Tax - Impuestos
				setTaxAmtPriceList(pc.getTaxAmtPriceList());
				setTaxAmtPriceStd(pc.getTaxAmtPriceStd());
				setTaxAmtPriceLimit(pc.getTaxAmtPriceLimit());
						    	
				priceListEntered = pc.getPriceListEntered();
				priceStdEntered = pc.getPriceStdEntered();
				priceLimitEntered = pc.getPriceLimitEntered();
				priceList = pc.getPriceList();
				priceStd = pc.getPriceStd();
				priceLimit = pc.getPriceLimit();
				
				// Calc Profit Prices - Calculos de Ganancias de Precios
				profitPriceListEntered = calcProfit(priceListEntered, priceBase);
				profitPriceStdEntered = calcProfit(priceStdEntered, priceBase);
				profitPriceLimitEntered = calcProfit(priceLimitEntered, priceBase);
				
				// Calc Profit Percentages - Calculos de Porcentajes de Precios
				if(percentageProfitPList.compareTo(BigDecimal.ZERO) == 0)
					percentageProfitPList = calcPercentageProfit(priceListEntered, profitPriceListEntered);
		    	percentageProfitPStd = calcPercentageProfit(priceStdEntered, profitPriceStdEntered);
		    	percentageProfitPLimit = calcPercentageProfit(priceLimitEntered, profitPriceLimitEntered);
		    	
		    	// Calc Profit - Calculo de Ganancias
		    	profitPriceList = calcProfit(priceList, priceBase);
		    	profitPriceStd = calcProfit(priceStd, priceBase);
		    	profitPriceLimit = calcProfit(priceLimit, priceBase);
		    	
		    	// Set Profit Prices Entered - Ganancias Ingresadas
		    	setBSCA_ProfitPriceListEntered(profitPriceListEntered);
		    	setBSCA_ProfitPriceStdEntered(profitPriceStdEntered);
		    	setBSCA_ProfitPriceLimitEntered(profitPriceLimitEntered);
		    	 
		    	// Set Profit Percentage - Porcentajes de Ganancias Enteradas
		    	setPercentageProfitPList(percentageProfitPList);
		    	setPercentageProfitPStd(percentageProfitPStd);
		    	setPercentageProfitPLimit(percentageProfitPLimit);
		    	
		    	// Set Profit Prices - Ganancias
		    	setBSCA_ProfitPriceList(profitPriceList);
		    	setBSCA_ProfitPriceStd(profitPriceStd);
		    	setBSCA_ProfitPriceLimit(profitPriceLimit);
		    	
		    	// Set Prices - Precios
		    	setPriceList(priceList);
		    	setPriceStd(priceStd);
		    	setPriceLimit(priceLimit);
		    	
		    	// Set Prices Entered - Precios Ingresados 
		    	setPriceListEntered(priceListEntered);
		    	setPriceStdEntered(priceStdEntered);
		    	setPriceLimitEntered(priceLimitEntered);
			}
		}
		return null;
	}
	
	public String setValues(BigDecimal priceListEntered) {
		String result = null;
		MProductPrice productPrice = new Query(getCtx(), MProductPrice.Table_Name, "M_ProductPrice.M_Product_ID=? AND M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault='Y' AND M_PriceList.AD_Org_ID=?", null)
			.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_ProductPrice.M_PriceList_Version_ID = M_PriceList_Version.M_PriceList_Version_ID")
			.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
			.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
			.setParameters(M_Product_ID, getAD_Org_ID()).first();

		if(productPrice != null)
			setPriceActual(productPrice.getPriceList());
		else {
		//	MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_ProductNotPriceList"));
		//	Object[] arguments = new Object[]{product.getName(), org.getName()};
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_BSCA_ProductValue_ID, null);
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_M_Product_ID, null);
		//	mTab.setValue(MBSCAPriceChange.COLUMNNAME_AD_OrgTrx_ID, null);
		//	mTab.dataRefresh();
		//	return mf.format(arguments);
			setPriceActual(Env.ZERO);
		}

		MPriceListVersion listVersion = null;
		if(getM_PriceList_Version_ID() != 0)
			listVersion = new MPriceListVersion(getCtx(), getM_PriceList_Version_ID(), get_TrxName());
		
		MDiscountSchema discountSchema = new Query(getCtx(), MDiscountSchema.Table_Name, "AD_Org_ID=? AND ValidFrom <= ?", null)
			.setOnlyActiveRecords(true).setParameters(getAD_Org_ID(), getDateAcct()).first();
		if(discountSchema == null) {
			MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
			MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_NotPriceListForOrg"));
			Object[] arguments = new Object[]{org.getName()};
			m_processMsg = mf.format(arguments);
			return m_processMsg;
		}
		MDiscountSchemaLine discountSchemaLine = new Query(getCtx(), MDiscountSchemaLine.Table_Name, "M_DiscountSchema_ID =? AND M_Product_ID =?", get_TrxName())
					.setOnlyActiveRecords(true).setParameters(discountSchema.getM_DiscountSchema_ID(), M_Product_ID).first();
		if(discountSchemaLine != null) {
//			priceListEntered = calcPrice(discountSchemaLine.getList_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_List_Base);
			priceStdEntered = calcPrice(discountSchemaLine.getStd_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_Std_Base);
			priceLimitEntered = calcPrice(discountSchemaLine.getLimit_Base(), productPrice, discountSchemaLine, getCtx(), listVersion, MDiscountSchemaLine.COLUMNNAME_Limit_Base);
			// Set Prices Type Bases
			setList_Base(discountSchemaLine.getList_Base());
			setStd_Base(discountSchemaLine.getStd_Base());
			setLimit_Base(discountSchemaLine.getLimit_Base());
		} else {
			result = "Producto " + getM_Product().getName() + " no tiene Esquema de Precio.";
		}
			
		
//		percentageProfitPList = getPercentageProfitPList();
//		if(percentageProfitPList.compareTo(BigDecimal.ZERO) > 0 && priceBase.compareTo(BigDecimal.ZERO) > 0) {
//			int precision = 2;
//			if(getC_Invoice() != null)
//				precision = getC_Invoice().getC_Currency().getStdPrecision();
//			priceListEntered = calcProfitFromPercentage(priceBase, percentageProfitPList, precision);
//		}
		
    	priceList = priceListEntered;
    	priceStd = priceStdEntered;
    	priceLimit = priceLimitEntered;
    	
		boolean isTaxIncluded = false;
		int scaleTax = 2;
		if(productPrice != null) {
			MPriceList SOPriceList = (MPriceList) productPrice.getM_PriceList_Version().getM_PriceList();
			isTaxIncluded = SOPriceList.isTaxIncluded();
			scaleTax = SOPriceList.getStandardPrecision();
		}
		
		MTax tax = new Query(getCtx(), MTax.Table_Name, "C_TaxCategory_ID=? AND IsDefault='Y' AND SOPOType!='P'", null)
			.setOnlyActiveRecords(true).setOrderBy("ValidFrom DESC").setParameters(getM_Product().getC_TaxCategory_ID()).first();
		if(tax != null) {
			taxAmtPriceList = tax.calculateTax(priceListEntered, isTaxIncluded, scaleTax);
			taxAmtPriceStd = tax.calculateTax(priceStdEntered, isTaxIncluded, scaleTax);
			taxAmtPriceLimit = tax.calculateTax(priceLimitEntered, isTaxIncluded, scaleTax);
			if(isTaxIncluded) {
				priceListWTax = priceListEntered;
				priceStdWTax = priceStdEntered;
				priceLimitWTax = priceLimitEntered;
			} else {
				priceListWTax = priceListEntered.add(taxAmtPriceList);
				priceStdWTax = priceStdEntered.add(taxAmtPriceStd);
				priceLimitWTax = priceLimitEntered.add(taxAmtPriceLimit);
			}
		} else
			m_processMsg = Msg.getMsg(getCtx(), "BSCA_NotTaxFound");
		
		// Set Price With Tax - Precios con Impuesto
		setPriceListWTax(priceListWTax);
		setPriceStdWTax(priceStdWTax);
		setPriceLimitWTax(priceLimitWTax);

		// Set Tax - Impuestos
		setTaxAmtPriceList(taxAmtPriceList);
		setTaxAmtPriceStd(taxAmtPriceStd);
		setTaxAmtPriceLimit(taxAmtPriceLimit);
		
		// Calc Profit Prices - Calculos de Ganancias de Precios
		profitPriceListEntered = calcProfit(priceListEntered, priceBase);
		profitPriceStdEntered = calcProfit(priceStdEntered, priceBase);
		profitPriceLimitEntered = calcProfit(priceLimitEntered, priceBase);
		
		// Calc Profit Percentages - Calculos de Porcentajes de Precios
		if(percentageProfitPList.compareTo(BigDecimal.ZERO) == 0)
			percentageProfitPList = calcPercentageProfit(priceListEntered, profitPriceListEntered);
    	percentageProfitPStd = calcPercentageProfit(priceStdEntered, profitPriceStdEntered);
    	percentageProfitPLimit = calcPercentageProfit(priceLimitEntered, profitPriceLimitEntered);
    	
    	// Calc Profit - Calculo de Ganancias
    	profitPriceList = calcProfit(priceList, priceBase);
    	profitPriceStd = calcProfit(priceStd, priceBase);
    	profitPriceLimit = calcProfit(priceLimit, priceBase);
    	
    	// Set Profit Prices Entered - Ganancias Ingresadas
    	setBSCA_ProfitPriceListEntered(profitPriceListEntered);
    	setBSCA_ProfitPriceStdEntered(profitPriceStdEntered);
    	setBSCA_ProfitPriceLimitEntered(profitPriceLimitEntered);
    	 
    	// Set Profit Percentage - Porcentajes de Ganancias Enteradas
    	setPercentageProfitPList(percentageProfitPList);
    	setPercentageProfitPStd(percentageProfitPStd);
    	setPercentageProfitPLimit(percentageProfitPLimit);
    	
    	// Set Profit Prices - Ganancias
    	setBSCA_ProfitPriceList(profitPriceList);
    	setBSCA_ProfitPriceStd(profitPriceStd);
    	setBSCA_ProfitPriceLimit(profitPriceLimit);
    	
    	// Set Prices - Precios
    	setPriceList(priceList);
    	setPriceStd(priceStd);
    	setPriceLimit(priceLimit);
    	
    	// Set Prices Entered - Precios Ingresados 
    	setPriceListEntered(priceListEntered);
    	setPriceStdEntered(priceStdEntered);
    	setPriceLimitEntered(priceLimitEntered);
    	return result;
	}	

	@Override
	public boolean processIt(String action) throws Exception {
		m_processMsg = null;
		DocumentEngine engine = new DocumentEngine (this, getDocStatus());
		return engine.processIt (action, getDocAction());
	}

	@Override
	public boolean unlockIt() {
		return true;
	}

	@Override
	public boolean invalidateIt() {
		return true;
	}

	@Override
	public String prepareIt() {
		System.out.println("Preparando Documento de Control de Cambio de Precio");
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_PREPARE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		m_justPrepared = true;
		if (!DOCACTION_Complete.equals(getDocAction()))
			setDocAction(DOCACTION_Complete);
		
		if(isVoidPrevDocs())
			voidItPrevDocs();
		if(DOCSTATUS_Drafted.equals(getDocStatus())) {
			m_processMsg = createForOtherOrgs();
			if (m_processMsg != null)
				return DocAction.STATUS_Invalid;
		}
	if(getBSCA_PreparedBy_ID() <= 0)
		setBSCA_PreparedBy_ID(getDoc_User_ID());	
	if(getDatePrepared() == null)
		setDatePrepared(new Timestamp(System.currentTimeMillis()));
	return DocAction.STATUS_InProgress;
	}

	private String createForOtherOrgs() {
		List<X_BSCA_PC_MultiOrgs> orgs = new Query(getCtx(), X_BSCA_PC_MultiOrgs.Table_Name, "BSCA_PriceChange_ID=?", get_TrxName())
						.setOnlyActiveRecords(true).setParameters(getBSCA_PriceChange_ID()).list();
		for(X_BSCA_PC_MultiOrgs pcOrg : orgs) {
			MBSCAPriceChange priceChance = new MBSCAPriceChange(getCtx(), getM_Product_ID(), null, null, pcOrg.getAD_Org_ID(), pcOrg.getAD_Org_ID(), get_TrxName());
			int BSCA_ProductValue_ID = DB.getSQLValue(get_TrxName(), "SELECT BSCA_ProductValue_ID FROM BSCA_ProductValue WHERE IsMasterValue='Y' AND M_Product_ID=?", 
					getM_Product_ID());
			priceChance.setBSCA_ProductValue_ID(BSCA_ProductValue_ID);
			priceChance.setM_PriceList_Version_ID(getM_PriceList_Version_ID());
			priceChance.setC_DocTypeTarget_ID(getC_DocTypeTarget_ID());
			priceChance.setIsSetPriceList(isSetPriceList());
			priceChance.setIsSetPriceStd(isSetPriceStd());
			priceChance.setIsSetPriceLimit(isSetPriceLimit());
			priceChance.setLastInvoice(pcOrg);
			priceChance.setLastOrder();
			priceChance.setPercentageProfitPList(getPercentageProfitPList());
			priceChance.setPercentageProfitPStd(getPercentageProfitPStd());
			priceChance.setPercentageProfitPLimit(getPercentageProfitPLimit());
			String val = priceChance.setValues(pcOrg);
			if(val != null)
				return val;
			if(priceChance.save()) {
				pcOrg.setBSCA_PCGenerated_ID(priceChance.getBSCA_PriceChange_ID());
				pcOrg.save();
				priceChance.setDocAction(MBSCAPriceChange.DOCACTION_Prepare);
				try {
					if(priceChance.processIt(MBSCAPriceChange.DOCACTION_Prepare)) {
						priceChance.save();
					} else
						return priceChance.getProcessMsg();
				} catch (Exception e) {
					return e.getMessage();
				}
			}
		}
		return null;
	}

	@Override
	public boolean approveIt() {
		if (log.isLoggable(Level.INFO)) log.info(toString());
		setIsApproved(true);
		return true;
	}

	@Override
	public boolean rejectIt() {
		return true;
	}

	@Override
	public String completeIt() {
		System.out.println("Completando Documento de Control de Cambio de Precio");
		if (log.isLoggable(Level.INFO)) log.info(toString());
		//	Re-Check
		if (!m_justPrepared) {
			String status = prepareIt();
			m_justPrepared = false;
			if (!DocAction.STATUS_InProgress.equals(status))
				return status;
		}
		
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_BEFORE_COMPLETE);
		if (m_processMsg != null)
			return DocAction.STATUS_Invalid;

		//	Implicit Approval
		if (!isApproved())
			approveIt();

		//	User Validation
		String valid = ModelValidationEngine.get().fireDocValidate(this, ModelValidator.TIMING_AFTER_COMPLETE);
		if (valid != null) {
			m_processMsg = valid;
			return DocAction.STATUS_Invalid;
		}
		
		// Ubicamos el Product Price Actual
		MProductPrice productPrice = new Query(getCtx(), MProductPrice.Table_Name, "M_ProductPrice.M_Product_ID=? AND M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault = 'Y' AND M_PriceList_Version.AD_Org_ID=?", get_TrxName())
			.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_ProductPrice.M_PriceList_Version_ID = M_PriceList_Version.M_PriceList_Version_ID")
			.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
			.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
			.setParameters(getM_Product_ID(), getAD_Org_ID()).first();
		
		// Si el producto no tiene Lista de Precios de Venta para la Organizacion, se le debe crear una
		if(productPrice == null && (isSetPriceList() || isSetPriceStd() || isSetPriceLimit())) {
	
			// Buscamos la version de Lista de Precios si no existe, la creamos
			MPriceListVersion priceListVersion = new Query(getCtx(), MPriceListVersion.Table_Name, "M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault='Y' AND M_PriceList_Version.AD_Org_ID=?", get_TrxName())
				.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
				.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
				.setParameters(getAD_Org_ID()).first();
			
			if(priceListVersion == null) {
				MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
				MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_NotPriceListVersion"));
				Object[] arguments = new Object[]{org.getName()};
				m_processMsg = mf.format(arguments); 
				return DocAction.STATUS_Invalid;
			}
			productPrice = new MProductPrice(getCtx(), priceListVersion.getM_PriceList_Version_ID(), getM_Product_ID(), get_TrxName());
			productPrice.setClientOrg(priceListVersion);
			if(!productPrice.save()) {
				String msg = null;
				ValueNamePair err = CLogger.retrieveError();
				String val = err != null ? Msg.translate(getCtx(), err.getValue()) : "";
				if (err != null)
					msg = (val != null ? val + ": " : "") + err.getName();
				if (msg == null || msg.length() == 0)
					msg = "SaveError";
				m_processMsg = msg;
				log.severe(msg);
				return DocAction.STATUS_Invalid;
			}
		}
		// Seteamos los Valores del Product Price a los Valores Anteriores del Control de Cambio de Precios
		if(productPrice != null) {
			setPriceListOld(productPrice.getPriceList());
			setPriceStdOld(productPrice.getPriceStd());
			setPriceLimitOld(productPrice.getPriceLimit());
			
			// Seteamos los nuevos Valores de Precios al Product Price.
			if(isSetPriceList())
				productPrice.setPriceList(getPriceListEntered());
			if(isSetPriceStd())
				productPrice.setPriceStd(getPriceStdEntered());
			if(isSetPriceLimit())
				productPrice.setPriceLimit(getPriceLimitEntered());
			productPrice.set_ValueOfColumn("BSCA_PriceChange_ID", getBSCA_PriceChange_ID());
			if(!productPrice.save()) {
				if(productPrice.get_ValueAsInt("BSCA_PriceChange_ID") != getBSCA_PriceChange_ID()) {
					String msg = null;
					ValueNamePair err = CLogger.retrieveError();
					String val = err != null ? Msg.translate(getCtx(), err.getValue()) : "";
					if (err != null)
						msg = (val != null ? val + ": " : "") + err.getName();
					if (msg == null || msg.length() == 0)
						msg = "SaveError";
					m_processMsg = msg;
					log.severe(msg);
					return DocAction.STATUS_Invalid;
				}
			}
		}
		this.setBSCA_PriceChangePrev_ID(getAD_Org_ID(), getM_Product_ID());

		//sendTxtScale();
		
		if(m_processMsg != null) {
			log.severe(m_processMsg);
			return DocAction.STATUS_Invalid;
		}
		
		setBSCA_CompletedBy_ID(getDoc_User_ID());
		setDateCompleted(new Timestamp(System.currentTimeMillis()));
		
		setC_DocType_ID(getC_DocTypeTarget_ID());
		setProcessed(true);
		setDocAction(DOCACTION_Close);
		m_processMsg = info.toString();
		return DocAction.STATUS_Completed;
	}

	/*private void sendTxtScale() {
		String sql = "SELECT BSCATypeStellar FROM M_Product WHERE M_Product_ID =? ";
		String typeStellar = DB.getSQLValueString(null, sql, getM_Product_ID());
		if("1".equals(typeStellar) || "2".equals(typeStellar)) {
			String className=BSCA_SendTxtScale.class.getName();
			Query q = new Query(getCtx(), MProcess.Table_Name, " ClassName=?", null).setOnlyActiveRecords(true)
					.setParameters(className);
			if(q != null && q.count() > 1) {
				String processValue = MSysConfig.getValue("BSCA_SendTxtScale_Process", BSCA_SendTxtScale.class.getSimpleName(), getAD_Client_ID(), getAD_Org_ID());
				q = new Query(getCtx(), MProcess.Table_Name, " Value=? ", get_TrxName()).setOnlyActiveRecords(true)
					.setParameters(processValue);
			}
			MProcess pr = null;
			if(q != null)
				pr = q.first();
			
			if (pr==null) {
		      log.warning("Process " + className + " does not exist. ");
		      setBSCA_SendTxtScale("Y");
			} else if(pr.getClassname() != null) {
				// Create an instance of the actual process class.
				BSCA_SendTxtScale process = new BSCA_SendTxtScale();

				MPInstance mpi = new MPInstance(getCtx(), pr.getAD_Process_ID(), getBSCA_PriceChange_ID());
				mpi.setClientOrg(this);
				mpi.save();
				
				String sqlOrgInfo = "SELECT BSCA_ExportPrintFormatGroup_ID FROM AD_OrgInfo WHERE AD_Org_ID = ? ";
				int BSCA_ExportPrintFormatGroup_ID = DB.getSQLValue(null, sqlOrgInfo, getAD_Org_ID()); 
				
				int seqNo = 10;
				for(MProcessPara para : pr.getParameters()) {
					if(para.getColumnName().equals("BSCA_ExportPrintFormatGroup_ID")) {
						mpi.createParameter(seqNo, para.getColumnName(), BSCA_ExportPrintFormatGroup_ID);
					} else if(para.getColumnName().equals("IsManual")) {
						mpi.createParameter(seqNo, para.getColumnName(), "N");
					} else if(para.getColumnName().equals("TimeOut")) {
						mpi.createParameter(seqNo, para.getColumnName(), para.getDefaultValue());
					}
					seqNo = seqNo+10;
				}
				
				// Connect the process to the process instance.
				ProcessInfo pi = new ProcessInfo("Envio de TXT a Balanza", pr.getAD_Process_ID(), MBSCAPriceChange.Table_ID, getBSCA_PriceChange_ID());
				pi.setAD_PInstance_ID(mpi.get_ID());
				
				log.info("Starting process " + pr.getName());
				
				try {
					if(!process.startProcess(getCtx(), pi, null)) {
						String errorMsg = "Error en Proceso " + className + ": " + pi.getSummary();
						log.severe(errorMsg);
						setBSCA_SendTxtScale("Y");
						setSummary(errorMsg);
					} else {
						setBSCA_SendTxtScale("N");
						setSummary(pi.getSummary());
					}
				} catch (IllegalAccessError e) {
					setBSCA_SendTxtScale("Y");
					m_processMsg = e.getMessage();
				}
			}
		} else
			setBSCA_SendTxtScale("N");
	}*/

	@Override
	public boolean voidIt() {
		System.out.println("Anulando Documento de Control de Cambio de Precio");
		if (log.isLoggable(Level.INFO)) log.info(toString());
		
		if (DOCSTATUS_Closed.equals(getDocStatus())
			|| DOCSTATUS_Reversed.equals(getDocStatus())
			|| DOCSTATUS_Voided.equals(getDocStatus()))
		{
			m_processMsg = "Document Closed: " + getDocStatus();
			setDocAction(DOCACTION_None);
			return false;
		}

		//	Not Processed
		if (DOCSTATUS_Drafted.equals(getDocStatus())
			|| DOCSTATUS_Invalid.equals(getDocStatus())
			|| DOCSTATUS_InProgress.equals(getDocStatus())
			|| DOCSTATUS_Approved.equals(getDocStatus())
			|| DOCSTATUS_NotApproved.equals(getDocStatus()) )
		{
			// Before Void
			m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_VOID);
			if (m_processMsg != null)
				return false;
			
		}

		// After Void
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_VOID);
		if (m_processMsg != null)
			return false;

//		MBSCAPriceChange lastPriceChange = new Query(getCtx(), MBSCAPriceChange.Table_Name, "AD_Org_ID=? AND M_Product_ID=? AND DocStatus = 'CO'", get_TrxName())
//				.setOnlyActiveRecords(true).setParameters(getAD_Org_ID(), getM_Product_ID()).setOrderBy("DateAcct DESC").first();
//		
//		if(get_ID() != lastPriceChange.get_ID()) {
//			MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_HaveVoidLastDocument"));
//			Object[] arguments = new Object[]{ lastPriceChange.getDocumentNo(), lastPriceChange.getDateAcct()};
//			m_processMsg = mf.format(arguments);
//			return false;
//		}
//		
//		// Ubicamos el Product Price Actual
//		MProductPrice productPrice = new Query(getCtx(), MProductPrice.Table_Name, "M_ProductPrice.M_Product_ID=? AND M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault = 'Y' AND M_PriceList_Version.AD_Org_ID=?", get_TrxName())
//		.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_ProductPrice.M_PriceList_Version_ID = M_PriceList_Version.M_PriceList_Version_ID")
//		.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
//		.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
//		.setParameters(getM_Product_ID(), getAD_Org_ID()).first();
//		
//		// Seteamos los Valores anteriores de Precios al Product Price.
//		if(isSetPriceList() && productPrice != null)
//			productPrice.setPriceList(getPriceListOld());
//		if(isSetPriceStd() && productPrice != null)
//			productPrice.setPriceStd(getPriceStdOld());
//		if(isSetPriceLimit() && productPrice != null)
//			productPrice.setPriceLimit(getPriceLimitOld());
//		if(productPrice != null)
//			productPrice.saveEx();
		
		setProcessed(true);
		setDocAction(DOCACTION_None);
		setDocStatus(DOCSTATUS_Voided);
		return true;
	}

	private void voidItPrevDocs() {
		List<MBSCAPriceChange> priceChanges = new Query(getCtx(), MBSCAPriceChange.Table_Name, "DocStatus IN ('DR', 'IP') AND IsFixPrice = 'N' AND AD_Org_ID = ? AND M_Product_ID =? AND "
				+ " DateAcct <= ? AND PriceListEntered <= ? AND BSCA_PriceChange_ID != ? ", get_TrxName()).setOnlyActiveRecords(true).setParameters(getAD_Org_ID(), getM_Product_ID(), 
						getDateAcct(), getPriceListEntered(), getBSCA_PriceChange_ID()).list();
		for(MBSCAPriceChange priceChange : priceChanges) {
			priceChange.voidIt();
			priceChange.saveEx();
		}
 	}
	
	@Override
	public boolean closeIt() {
		return true;
	}

	@Override
	public boolean reverseCorrectIt() {
		System.out.println("Reversando Documento de Control de Cambio de Precio: " + getDocumentNo());
		if (log.isLoggable(Level.INFO)) log.info(toString());
		// Before reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_BEFORE_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		// After reverseCorrect
		m_processMsg = ModelValidationEngine.get().fireDocValidate(this,ModelValidator.TIMING_AFTER_REVERSECORRECT);
		if (m_processMsg != null)
			return false;

		MBSCAPriceChange lastPriceChange = new Query(getCtx(), MBSCAPriceChange.Table_Name, "AD_Org_ID=? AND M_Product_ID=? AND DocStatus = 'CO'", get_TrxName())
				.setOnlyActiveRecords(true).setParameters(getAD_Org_ID(), getM_Product_ID()).setOrderBy("ProcessedOn DESC").first();

		if(get_ID() != lastPriceChange.get_ID()) {
			MessageFormat mf = new MessageFormat(Msg.getMsg(Env.getAD_Language(Env.getCtx()), "BSCA_HaveVoidLastDocument"));
			Object[] arguments = new Object[]{ lastPriceChange.getDocumentNo(), lastPriceChange.getDateAcct()};
			m_processMsg = mf.format(arguments);
			return false;
		}

		// Ubicamos el Product Price Actual
		MProductPrice productPrice = new Query(getCtx(), MProductPrice.Table_Name, "M_ProductPrice.M_Product_ID=? AND M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault = 'Y' AND M_PriceList_Version.AD_Org_ID=?", get_TrxName())
			.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_ProductPrice.M_PriceList_Version_ID = M_PriceList_Version.M_PriceList_Version_ID")
			.addJoinClause("JOIN M_PriceList M_PriceList ON M_PriceList.M_PriceList_ID = M_PriceList_Version.M_PriceList_ID")
			.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
			.setParameters(getM_Product_ID(), getAD_Org_ID()).first();
		
		// Seteamos los Valores anteriores de Precios al Product Price.
		if(isSetPriceList() && productPrice != null)
			productPrice.setPriceList(getPriceListOld());
		if(isSetPriceStd() && productPrice != null)
			productPrice.setPriceStd(getPriceStdOld());
		if(isSetPriceLimit() && productPrice != null)
			productPrice.setPriceLimit(getPriceLimitOld());
		if(productPrice != null) {
			if(!productPrice.save()) {
				String msg = null;
				ValueNamePair err = CLogger.retrieveError();
				String val = err != null ? Msg.translate(getCtx(), err.getValue()) : "";
				if (err != null)
					msg = (val != null ? val + ": " : "") + err.getName();
				if (msg == null || msg.length() == 0)
					msg = "SaveError";
				m_processMsg = msg;
				log.severe(msg);
				return false;
			}
		}
		
		m_processMsg = getDocumentNo();
		setDocAction(DOCACTION_None);
		return true;
	}

	@Override
	public boolean reverseAccrualIt() {
		return true;
	}

	@Override
	public boolean reActivateIt() {
		return true;
	}

	@Override
	public String getSummary() {
		return m_summary;
	}
	
	public void setSummary(String summary) {
		m_summary = summary;
	}

	@Override
	public String getDocumentInfo() {
		MDocType dt = MDocType.get(getCtx(), getC_DocType_ID() > 0 ? getC_DocType_ID() : getC_DocTypeTarget_ID());
		return dt.getNameTrl() + " " + getDocumentNo();
	}

	@Override
	public File createPDF() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProcessMsg() {
		return m_processMsg;
	}

	@Override
	public int getDoc_User_ID() {
		return Env.getAD_User_ID(getCtx());
	}

	@Override
	public int getC_Currency_ID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public BigDecimal getApprovalAmt() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 	Called before Save for Pre-Save Operation
	 * 	@param newRecord new record
	 *	@return true if record can be saved
	 */
	@Override
	protected boolean beforeSave(boolean newRecord) {
		MOrg org = new MOrg(getCtx(), getAD_Org_ID(), get_TrxName());
		if(getAD_Org_ID() == 0 || org.isSummary()) {
			m_processMsg = Msg.getMsg(getCtx(), "BSCA_InvalidOrg");
			return false;
		}
		if(getM_Product_ID() > 0) {
			String sqlBOM = "SELECT M_Product_ID FROM M_Product_BOM WHERE M_ProductBOM_ID = ? AND IsProductShrinkage = 'Y'";
			int product_ID = DB.getSQLValue(get_TrxName(), sqlBOM, getM_Product_ID());
			if(product_ID > 0) {
				MProduct product = new MProduct(getCtx(), product_ID, get_TrxName());
				m_processMsg = "El Producto Seleccionado es una Merma del Producto: " + product.getName();
				return false;
			}
		}
		// Set Levels
		MProduct product = (MProduct) getM_Product();
		setUser1W_ID(product.get_ValueAsInt(MBSCAPriceChange.COLUMNNAME_User1W_ID));
		setUser1X_ID(product.get_ValueAsInt(MBSCAPriceChange.COLUMNNAME_User1X_ID));
		setUser1Y_ID(product.get_ValueAsInt(MBSCAPriceChange.COLUMNNAME_User1Y_ID));
		setUser1Z_ID(product.get_ValueAsInt(MBSCAPriceChange.COLUMNNAME_User1Z_ID));
		setUser1_ID(product.get_ValueAsInt(MBSCAPriceChange.COLUMNNAME_User1_ID));
		
		this.setBSCA_PriceChangePrev_ID(getAD_Org_ID(), getM_Product_ID());
		this.setBSCA_QtyCurrent(getAD_Org_ID(), getM_Product_ID());
		
		if(getAD_OrgTrx_ID() <= 0)
			setAD_OrgTrx_ID(getAD_Org_ID());
		
		set_ValueOfColumn("IsEcommerce", product.get_ValueAsBoolean("IsEcommerce"));
		
		return true;
	}	//	beforeSave

	@Override
	public int customizeValidActions(String docStatus, Object processing,
			String orderType, String isSOTrx, int AD_Table_ID,
			String[] docAction, String[] options, int index) {
		
		if (options == null)
			throw new IllegalArgumentException("Option array parameter is null");
		if (docAction == null)
			throw new IllegalArgumentException("Doc action array parameter is null");

		// If a document is drafted or invalid, the users are able to complete, prepare or void
		if (docStatus.equals(DocumentEngine.STATUS_Drafted) || docStatus.equals(DocumentEngine.STATUS_Invalid)) {
			options[index++] = DocumentEngine.ACTION_Complete;
			options[index++] = DocumentEngine.ACTION_Prepare;
			options[index++] = DocumentEngine.ACTION_Void;

			// If the document is already completed, we also want to be able to reactivate or void it instead of only closing it
		} else if (docStatus.equals(DocumentEngine.STATUS_Completed)) {
			options[index++] = DocumentEngine.ACTION_Reverse_Correct;
		} else if(docStatus.equals(DocumentEngine.STATUS_InProgress))
			options[index++] = DocumentEngine.ACTION_Void;

		return index;
	}
	
	/**
	 * Calculo de Precio
	 * 
	 * @param AD_Org_ID
	 * @param base
	 * @param productPrice
	 * @param product
	 * @param discountSchemaLine
	 * @param ctx
	 * @param mTab
	 * @param listVersion
	 * @return
	 */
	public BigDecimal calcPrice(String base, MProductPrice productPrice, MDiscountSchemaLine discountSchemaLine, Properties ctx, MPriceListVersion listVersion, String typeOfPrice) {
		BigDecimal result = Env.ZERO;
		MPriceList priceList = null;
		if(productPrice != null)
			priceList = (MPriceList)productPrice.getM_PriceList_Version().getM_PriceList();
		int C_Currency_ID = Env.getContextAsInt(ctx, "#C_Currency_ID");
		MCurrency currency = new MCurrency(ctx, C_Currency_ID, null);
		int precision = 2;
		if(priceList != null) {
			precision = priceList.getStandardPrecision();
			currency = (MCurrency) priceList.getC_Currency();
		}
		
		BigDecimal convertAmount = null;

		MProductPrice productPriceBase = null;
		MPriceList priceListFrom = null;
		if(listVersion != null) {
			priceListFrom = listVersion.getPriceList();
			productPriceBase = new Query(ctx, MProductPrice.Table_Name, "M_PriceList_Version_ID=? AND M_Product_ID=?", null)
							.setOnlyActiveRecords(true).setParameters(listVersion.get_ID(), getM_Product_ID()).first();
		}
		BigDecimal addAmt = Env.ZERO;
		BigDecimal discount = Env.ZERO;
		BigDecimal fixedPrice = Env.ZERO;
		if(MDiscountSchemaLine.COLUMNNAME_List_Base.equalsIgnoreCase(typeOfPrice)) {
			if(discountSchemaLine != null) {
				addAmt = discountSchemaLine.getList_AddAmt();
				discount = discountSchemaLine.getList_Discount();
				fixedPrice = discountSchemaLine.getList_Fixed();
			}
		} else if(MDiscountSchemaLine.COLUMNNAME_Std_Base.equalsIgnoreCase(typeOfPrice)) {
			if(discountSchemaLine != null) {
				addAmt = discountSchemaLine.getStd_AddAmt();
				discount = discountSchemaLine.getStd_Discount();
				fixedPrice = discountSchemaLine.getStd_Fixed();
			}
		} else if(MDiscountSchemaLine.COLUMNNAME_Limit_Base.equalsIgnoreCase(typeOfPrice)) {
			if(discountSchemaLine != null) {
				addAmt = discountSchemaLine.getLimit_AddAmt();
				discount = discountSchemaLine.getLimit_Discount();
				fixedPrice = discountSchemaLine.getLimit_Fixed();
			}
		}
		
		int C_ConversionType_ID = 0;
		if(discountSchemaLine != null)
			C_ConversionType_ID = discountSchemaLine.getC_ConversionType_ID();

		
		if(MDiscountSchemaLine.LIST_BASE_ListPrice.equalsIgnoreCase(base)) {
			if(productPriceBase != null) {
				priceBase = productPriceBase.getPriceStd();
				convertAmount = currencyConvert(priceBase, (MCurrency) priceListFrom.getC_Currency(), currency, 
						ctx, C_ConversionType_ID, precision);
				if(convertAmount == null)
					result = priceBase.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
				else
					result = convertAmount.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			}
			else
				result = result.add(addAmt)
					.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
		} 
		else if(MDiscountSchemaLine.LIST_BASE_StandardPrice.equalsIgnoreCase(base)) {
			if(productPriceBase != null) {
				priceBase = productPriceBase.getPriceStd();
				convertAmount = currencyConvert(priceBase, (MCurrency) priceListFrom.getC_Currency(), currency, 
						ctx, C_ConversionType_ID, precision);
				if(convertAmount == null)
					result = priceBase.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
				else
					result = convertAmount.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			}
			else
				result = result.add(addAmt)
					.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
		}
		else if(MDiscountSchemaLine.LIST_BASE_LimitPOPrice.equalsIgnoreCase(base)) {
			if(productPriceBase != null) {
				priceBase = productPriceBase.getPriceLimit();
				convertAmount = currencyConvert(priceBase, (MCurrency) priceListFrom.getC_Currency(), currency, 
						ctx, C_ConversionType_ID, precision);
				if(convertAmount == null)
					result = priceBase.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
				else
					result = convertAmount.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			}
			else
				result = result.add(addAmt)
					.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
		}
		else if(MDiscountSchemaLine.LIST_BASE_FixedPrice.equals(base)) {
			result = fixedPrice;
		}
		
		/**	 	Ultima Factura o Mayor Precio de Factura	**/
		else if("I".equals(base) || "H".equals(base)) {	
			if(invoiceLine == null) {
				if(priceBase != null && priceBase.compareTo(BigDecimal.ZERO) > 0)
					result = priceBase;
				else
					result = Env.ZERO;
			} else {
					result = invoiceLine.getPriceActual();
				convertAmount = currencyConvert(result, (MCurrency)invoiceLine.getC_Invoice().getC_Currency(), currency, 
					ctx, C_ConversionType_ID, precision);
			}
			if(convertAmount == null) {
				result = result.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			} else {
				result = convertAmount.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			}			
		}
		/**	 	Ultima Orden de Compra		**/
		else if("O".equals(base)) {
			MOrderLine orderLine = new Query(ctx, MOrderLine.Table_Name, "C_Order.IsSOTrx = 'N' AND C_Order.DocStatus = 'CO' AND C_Order.AD_Org_ID = ? AND C_OrderLine.M_Product_ID = ?", null)
				.addJoinClause("JOIN C_Order C_Order ON C_OrderLine.C_Order_ID = C_Order.C_Order_ID")
				.setOnlyActiveRecords(true).setOrderBy("C_Order.DateAcct DESC, C_Order.ProcessedOn DESC")
				.setParameters(getAD_OrgTrx_ID(), M_Product_ID).first();
			
			if(orderLine == null)
				result = Env.ZERO;
			else {
					result = orderLine.getPriceActual();
				convertAmount = currencyConvert(result, (MCurrency)orderLine.getC_Order().getC_Currency(), currency, 
					ctx, C_ConversionType_ID, precision);
			}
			if(convertAmount == null) {
				result = result.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			} else {
				result = convertAmount.add(addAmt)
						.multiply(Env.ONE.subtract(discount.divide(new BigDecimal(100))));
			}
		}
		return result.setScale(precision, RoundingMode.HALF_UP);
	}
	
	/**
	 * Conversion de Moneda
	 * @param amount
	 * @param currencyFrom
	 * @param currencyTo
	 * @param ctx
	 * @param conversionType
	 * @param mTab
	 * @return
	 */
	public BigDecimal currencyConvert(BigDecimal amount, MCurrency currencyFrom, MCurrency currencyTo, Properties ctx, int conversionType, int precision) {
		if(amount == null || currencyFrom == null || currencyTo == null)
			return null;
		if(amount.compareTo(Env.ZERO) == 0 || currencyFrom.equals(currencyTo))
			return amount;
		
		BigDecimal amountConverted = Env.ZERO;
		
		MConversionRate conversionRate = new Query(ctx, MConversionRate.Table_Name, "C_Currency_ID=? AND C_Currency_ID_To=? AND C_ConversionType_ID=?"
				+ " AND ? BETWEEN ValidFrom AND ValidTo AND	AD_Client_ID IN (0,?) AND AD_Org_ID IN (0,?)", null)
				.setOrderBy("AD_Client_ID DESC, AD_Org_ID DESC, ValidFrom DESC")
				.setOnlyActiveRecords(true).setParameters(currencyFrom.get_ID(), currencyTo.get_ID(), conversionType, getDateAcct(), 
						getAD_Org_ID(), getAD_Client_ID()).first();
		if(conversionRate == null)
			return amount;
		
		amountConverted = amount.multiply(conversionRate.getMultiplyRate()).setScale(precision, RoundingMode.HALF_UP);
		return amountConverted;
	}
	
	private  BigDecimal calcProfit(BigDecimal priceEntered, BigDecimal pricesLastInv) {
		MPriceList priceList = new Query(getCtx(), MPriceList.Table_Name, "M_PriceList.IsSOPriceList='Y' AND M_PriceList.IsDefault='Y' AND M_PriceList.AD_Org_ID=?", null)
			.addJoinClause("JOIN M_PriceList_Version ON M_PriceList_Version.M_PriceList_ID = M_PriceList.M_PriceList_ID")
		    .setOrderBy("M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
		    .setParameters(getAD_Org_ID()).first();
		int precision=2;
		if(priceList!=null)
			precision=priceList.getStandardPrecision();
		return priceEntered.subtract(pricesLastInv).setScale(precision, RoundingMode.HALF_UP);	
	}
	
	private BigDecimal calcPercentageProfit(BigDecimal value, BigDecimal profit) {
		MPriceList purchasePriceList = new Query(getCtx(), MPriceList.Table_Name, "M_PriceList.IsSOPriceList='N' AND M_PriceList.IsDefault='Y' AND (M_PriceList.AD_Org_ID=? OR M_PriceList.AD_Org_ID=0)", null)
			.addJoinClause("JOIN M_PriceList_Version M_PriceList_Version ON M_PriceList_Version.M_PriceList_ID = M_PriceList.M_PriceList_ID")
			.setOrderBy(" Order By M_PriceList_Version.ValidFrom DESC").setOnlyActiveRecords(true)
			.setParameters(getAD_Org_ID()).first();
		BigDecimal percentage = BigDecimal.ZERO;
		int precision = 5;
		if(purchasePriceList != null)
			precision = purchasePriceList.getStandardPrecision();
	    if(value.compareTo(BigDecimal.ZERO)==0)
	    	if(profit.compareTo(BigDecimal.ZERO) > 0)
	    		percentage=BigDecimal.valueOf(100);
	    	else
	    		percentage=BigDecimal.ZERO;
	    else {
	    	profit = profit.multiply(BigDecimal.valueOf(100));
	    	percentage=profit.divide(value, precision, RoundingMode.HALF_UP);
	    }
		return percentage;
	}
	
	public void setBSCA_PriceChangePrev_ID(int m_AD_Org_ID, int m_M_Product_ID){
		String sql = "SELECT BSCA_PriceChange_ID FROM BSCA_PriceChange "
				+ " WHERE DocStatus = 'CO' AND IsActive = 'Y' AND AD_Org_ID = ? AND M_Product_ID=? "
				+ " ORDER BY DateAcct DESC ";
		int BSCA_PriceChangePrev_ID = DB.getSQLValue(null, sql, m_AD_Org_ID, m_M_Product_ID);
		super.setBSCA_PriceChangePrev_ID(BSCA_PriceChangePrev_ID);
	}
	
	private void setBSCA_QtyCurrent(int m_AD_Org_ID, int m_M_Product_ID) {
		BigDecimal BSCA_QtyCurrent = BigDecimal.ZERO;
		MWarehouse[] warehouses = MWarehouse.getForOrg(getCtx(), m_AD_Org_ID);
		
		for(MWarehouse warehouse : warehouses) {
			BSCA_QtyCurrent = BSCA_QtyCurrent.add(MStorageOnHand.getQtyOnHand(m_M_Product_ID, warehouse.getM_Warehouse_ID(), 0, get_TrxName()));
		}
		String sql = "SELECT SUM(QtyWait) FROM BSCA_RouteOutWait WHERE AD_Org_ID=? AND M_Product_ID=?";
		BigDecimal QtyWait = DB.getSQLValueBD(get_TrxName(), sql, m_AD_Org_ID, m_M_Product_ID);
		if(QtyWait == null)
			QtyWait = BigDecimal.ZERO;
		BigDecimal QtyDocs = BigDecimal.ZERO;
		if(getM_InOut_ID() != 0) {
			MInOut inOut = new MInOut(getCtx(), getM_InOut_ID(), get_TrxName());
			List<MInOutLine> lines = new Query(getCtx(), MInOutLine.Table_Name, "M_InOut_ID=? AND M_Product_ID=? ", get_TrxName())
					.setOnlyActiveRecords(true).setParameters(inOut.getM_InOut_ID(), m_M_Product_ID).list();
			for(MInOutLine line : lines)
				QtyDocs = QtyDocs.add(line.getMovementQty());
		} else if(getM_MovementConfirm_ID() != 0) {
			MMovementConfirm confirm = new MMovementConfirm(getCtx(), getM_MovementConfirm_ID(), get_TrxName());
			List<MMovementLine> movementLines = new Query(getCtx(), MMovementLine.Table_Name, " M_MovementLineConfirm.M_MovementConfirm_ID=? AND M_MovementLine.AD_Org_ID=? "
					+ " AND M_MovementLine.M_Product_ID=? ", get_TrxName()).setOnlyActiveRecords(true)
					.addJoinClause("JOIN M_MovementLineConfirm ON M_MovementLineConfirm.M_MovementLine_ID = M_MovementLine.M_MovementLine_ID")
					.setParameters(confirm.getM_Movement_ID(), m_AD_Org_ID, m_M_Product_ID).list();
			for(MMovementLine line : movementLines)
				QtyDocs.add(line.getConfirmedQty().subtract(line.getScrappedQty()));
		}
		BSCA_QtyCurrent = BSCA_QtyCurrent.subtract(QtyWait).subtract(QtyDocs);
		if(BSCA_QtyCurrent.signum() == -1)
			BSCA_QtyCurrent = BigDecimal.ZERO;
		super.setBSCA_QtyCurrent(BSCA_QtyCurrent);
	}
	
	private BigDecimal calcProfitFromPercentage(BigDecimal priceLastInvoice, BigDecimal percentage, int precision) {
		BigDecimal profit = BigDecimal.ZERO;
		profit = priceLastInvoice.divide(BigDecimal.ONE.subtract(percentage.divide(BigDecimal.valueOf(100))), precision, RoundingMode.HALF_UP);
		return profit;
	}
}
