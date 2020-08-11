/******************************************************************************
 * Product: iDempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2012 ComPiere, Inc. All Rights Reserved.                *
 * This program is free software, you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY, without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program, if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 * For the text or an alternative of this public license, you may reach us    *
 * ComPiere, Inc., 2620 Augustine Dr. #245, Santa Clara, CA 95054, USA        *
 * or via info@compiere.org or http://www.compiere.org/license.html           *
 *****************************************************************************/
/** Generated Model - DO NOT CHANGE */
package com.bucaresystems.model;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for BSCA_PriceChange
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_BSCA_PriceChange extends PO implements I_BSCA_PriceChange, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20191208L;

    /** Standard Constructor */
    public X_BSCA_PriceChange (Properties ctx, int BSCA_PriceChange_ID, String trxName)
    {
      super (ctx, BSCA_PriceChange_ID, trxName);
      /** if (BSCA_PriceChange_ID == 0)
        {
			setBSCA_PriceChange_ID (0);
			setBSCA_ProductValue_ID (0);
			setC_DocType_ID (0);
// 0
			setC_DocTypeTarget_ID (0);
// @SQL=SELECT dt.C_DocType_ID FROM C_DocType dt WHERE DocBaseType='PCH' AND IsDefault='Y'
			setDocAction (null);
// CO
			setDocStatus (null);
// DR
			setDocumentNo (null);
			setIsFixPrice (null);
			setIsSetPriceLimit (false);
// N
			setIsSetPriceList (false);
// N
			setIsSetPriceStd (false);
// N
			setIsVoidPrevDocs (true);
// Y
			setM_Product_ID (0);
			setProcessed (false);
        } */
    }

    /** Load Constructor */
    public X_BSCA_PriceChange (Properties ctx, ResultSet rs, String trxName)
    {
      super (ctx, rs, trxName);
    }

    /** AccessLevel
      * @return 3 - Client - Org 
      */
    protected int get_AccessLevel()
    {
      return accessLevel.intValue();
    }

    /** Load Meta Data */
    protected POInfo initPO (Properties ctx)
    {
      POInfo poi = POInfo.getPOInfo (ctx, Table_ID, get_TrxName());
      return poi;
    }

    public String toString()
    {
      StringBuffer sb = new StringBuffer ("X_BSCA_PriceChange[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	/** Set Trx Organization.
		@param AD_OrgTrx_ID 
		Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID)
	{
		if (AD_OrgTrx_ID < 1) 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, null);
		else 
			set_Value (COLUMNNAME_AD_OrgTrx_ID, Integer.valueOf(AD_OrgTrx_ID));
	}

	/** Get Trx Organization.
		@return Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_AD_OrgTrx_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getBSCA_CompletedBy() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getBSCA_CompletedBy_ID(), get_TrxName());	}

	/** Set BSCA_CompletedBy_ID.
		@param BSCA_CompletedBy_ID BSCA_CompletedBy_ID	  */
	public void setBSCA_CompletedBy_ID (int BSCA_CompletedBy_ID)
	{
		if (BSCA_CompletedBy_ID < 1) 
			set_Value (COLUMNNAME_BSCA_CompletedBy_ID, null);
		else 
			set_Value (COLUMNNAME_BSCA_CompletedBy_ID, Integer.valueOf(BSCA_CompletedBy_ID));
	}

	/** Get BSCA_CompletedBy_ID.
		@return BSCA_CompletedBy_ID	  */
	public int getBSCA_CompletedBy_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_CompletedBy_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_AD_User getBSCA_PreparedBy() throws RuntimeException
    {
		return (org.compiere.model.I_AD_User)MTable.get(getCtx(), org.compiere.model.I_AD_User.Table_Name)
			.getPO(getBSCA_PreparedBy_ID(), get_TrxName());	}

	/** Set BSCA_PreparedBy_ID.
		@param BSCA_PreparedBy_ID BSCA_PreparedBy_ID	  */
	public void setBSCA_PreparedBy_ID (int BSCA_PreparedBy_ID)
	{
		if (BSCA_PreparedBy_ID < 1) 
			set_Value (COLUMNNAME_BSCA_PreparedBy_ID, null);
		else 
			set_Value (COLUMNNAME_BSCA_PreparedBy_ID, Integer.valueOf(BSCA_PreparedBy_ID));
	}

	/** Get BSCA_PreparedBy_ID.
		@return BSCA_PreparedBy_ID	  */
	public int getBSCA_PreparedBy_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_PreparedBy_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Price Change.
		@param BSCA_PriceChange_ID Price Change	  */
	public void setBSCA_PriceChange_ID (int BSCA_PriceChange_ID)
	{
		if (BSCA_PriceChange_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BSCA_PriceChange_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BSCA_PriceChange_ID, Integer.valueOf(BSCA_PriceChange_ID));
	}

	/** Get Price Change.
		@return Price Change	  */
	public int getBSCA_PriceChange_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_PriceChange_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PriceChangePrev() throws RuntimeException
    {
		return (com.bucaresystems.model.I_BSCA_PriceChange)MTable.get(getCtx(), com.bucaresystems.model.I_BSCA_PriceChange.Table_Name)
			.getPO(getBSCA_PriceChangePrev_ID(), get_TrxName());	}

	/** Set Price Change Previus.
		@param BSCA_PriceChangePrev_ID Price Change Previus	  */
	public void setBSCA_PriceChangePrev_ID (int BSCA_PriceChangePrev_ID)
	{
		if (BSCA_PriceChangePrev_ID < 1) 
			set_Value (COLUMNNAME_BSCA_PriceChangePrev_ID, null);
		else 
			set_Value (COLUMNNAME_BSCA_PriceChangePrev_ID, Integer.valueOf(BSCA_PriceChangePrev_ID));
	}

	/** Get Price Change Previus.
		@return Price Change Previus	  */
	public int getBSCA_PriceChangePrev_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_PriceChangePrev_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BSCA_PriceChange_UU.
		@param BSCA_PriceChange_UU BSCA_PriceChange_UU	  */
	public void setBSCA_PriceChange_UU (String BSCA_PriceChange_UU)
	{
		set_Value (COLUMNNAME_BSCA_PriceChange_UU, BSCA_PriceChange_UU);
	}

	/** Get BSCA_PriceChange_UU.
		@return BSCA_PriceChange_UU	  */
	public String getBSCA_PriceChange_UU () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_PriceChange_UU);
	}

	/** Set Print Price.
		@param BSCA_PrintPrice Print Price	  */
	public void setBSCA_PrintPrice (String BSCA_PrintPrice)
	{
		set_Value (COLUMNNAME_BSCA_PrintPrice, BSCA_PrintPrice);
	}

	/** Get Print Price.
		@return Print Price	  */
	public String getBSCA_PrintPrice () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_PrintPrice);
	}

	/** Set Print Price 2.
		@param BSCA_PrintPrice2 Print Price 2	  */
	public void setBSCA_PrintPrice2 (String BSCA_PrintPrice2)
	{
		set_Value (COLUMNNAME_BSCA_PrintPrice2, BSCA_PrintPrice2);
	}

	/** Get Print Price 2.
		@return Print Price 2	  */
	public String getBSCA_PrintPrice2 () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_PrintPrice2);
	}

	public com.bucaresystems.model.I_BSCA_ProductValue getBSCA_ProductValue() throws RuntimeException
    {
		return (com.bucaresystems.model.I_BSCA_ProductValue)MTable.get(getCtx(), com.bucaresystems.model.I_BSCA_ProductValue.Table_Name)
			.getPO(getBSCA_ProductValue_ID(), get_TrxName());	}

	/** Set BSCA_ProductValue.
		@param BSCA_ProductValue_ID BSCA_ProductValue	  */
	public void setBSCA_ProductValue_ID (int BSCA_ProductValue_ID)
	{
		if (BSCA_ProductValue_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BSCA_ProductValue_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BSCA_ProductValue_ID, Integer.valueOf(BSCA_ProductValue_ID));
	}

	/** Get BSCA_ProductValue.
		@return BSCA_ProductValue	  */
	public int getBSCA_ProductValue_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_ProductValue_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Profit Price Limit.
		@param BSCA_ProfitPriceLimit Profit Price Limit	  */
	public void setBSCA_ProfitPriceLimit (BigDecimal BSCA_ProfitPriceLimit)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceLimit, BSCA_ProfitPriceLimit);
	}

	/** Get Profit Price Limit.
		@return Profit Price Limit	  */
	public BigDecimal getBSCA_ProfitPriceLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Profit Price Limit Entered.
		@param BSCA_ProfitPriceLimitEntered Profit Price Limit Entered	  */
	public void setBSCA_ProfitPriceLimitEntered (BigDecimal BSCA_ProfitPriceLimitEntered)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceLimitEntered, BSCA_ProfitPriceLimitEntered);
	}

	/** Get Profit Price Limit Entered.
		@return Profit Price Limit Entered	  */
	public BigDecimal getBSCA_ProfitPriceLimitEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceLimitEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Profit Price List.
		@param BSCA_ProfitPriceList Profit Price List	  */
	public void setBSCA_ProfitPriceList (BigDecimal BSCA_ProfitPriceList)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceList, BSCA_ProfitPriceList);
	}

	/** Get Profit Price List.
		@return Profit Price List	  */
	public BigDecimal getBSCA_ProfitPriceList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Profit Price List Entered.
		@param BSCA_ProfitPriceListEntered Profit Price List Entered	  */
	public void setBSCA_ProfitPriceListEntered (BigDecimal BSCA_ProfitPriceListEntered)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceListEntered, BSCA_ProfitPriceListEntered);
	}

	/** Get Profit Price List Entered.
		@return Profit Price List Entered	  */
	public BigDecimal getBSCA_ProfitPriceListEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceListEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Profit Price Std.
		@param BSCA_ProfitPriceStd Profit Price Std	  */
	public void setBSCA_ProfitPriceStd (BigDecimal BSCA_ProfitPriceStd)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceStd, BSCA_ProfitPriceStd);
	}

	/** Get Profit Price Std.
		@return Profit Price Std	  */
	public BigDecimal getBSCA_ProfitPriceStd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceStd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Profit Price Std Entered.
		@param BSCA_ProfitPriceStdEntered Profit Price Std Entered	  */
	public void setBSCA_ProfitPriceStdEntered (BigDecimal BSCA_ProfitPriceStdEntered)
	{
		set_Value (COLUMNNAME_BSCA_ProfitPriceStdEntered, BSCA_ProfitPriceStdEntered);
	}

	/** Get Profit Price Std Entered.
		@return Profit Price Std Entered	  */
	public BigDecimal getBSCA_ProfitPriceStdEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_ProfitPriceStdEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Qty Current.
		@param BSCA_QtyCurrent Qty Current	  */
	public void setBSCA_QtyCurrent (BigDecimal BSCA_QtyCurrent)
	{
		set_Value (COLUMNNAME_BSCA_QtyCurrent, BSCA_QtyCurrent);
	}

	/** Get Qty Current.
		@return Qty Current	  */
	public BigDecimal getBSCA_QtyCurrent () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_BSCA_QtyCurrent);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Send Txt Scale.
		@param BSCA_SendTxtScale Send Txt Scale	  */
	public void setBSCA_SendTxtScale (String BSCA_SendTxtScale)
	{
		set_Value (COLUMNNAME_BSCA_SendTxtScale, BSCA_SendTxtScale);
	}

	/** Get Send Txt Scale.
		@return Send Txt Scale	  */
	public String getBSCA_SendTxtScale () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_SendTxtScale);
	}

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getC_DocType_ID(), get_TrxName());	}

	/** Set Document Type.
		@param C_DocType_ID 
		Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID)
	{
		if (C_DocType_ID < 0) 
			set_Value (COLUMNNAME_C_DocType_ID, null);
		else 
			set_Value (COLUMNNAME_C_DocType_ID, Integer.valueOf(C_DocType_ID));
	}

	/** Get Document Type.
		@return Document type or rules
	  */
	public int getC_DocType_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocType_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_DocType getC_DocTypeTarget() throws RuntimeException
    {
		return (org.compiere.model.I_C_DocType)MTable.get(getCtx(), org.compiere.model.I_C_DocType.Table_Name)
			.getPO(getC_DocTypeTarget_ID(), get_TrxName());	}

	/** Set Target Document Type.
		@param C_DocTypeTarget_ID 
		Target document type for conversing documents
	  */
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID)
	{
		if (C_DocTypeTarget_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_DocTypeTarget_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_DocTypeTarget_ID, Integer.valueOf(C_DocTypeTarget_ID));
	}

	/** Get Target Document Type.
		@return Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_DocTypeTarget_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException
    {
		return (org.compiere.model.I_C_Invoice)MTable.get(getCtx(), org.compiere.model.I_C_Invoice.Table_Name)
			.getPO(getC_Invoice_ID(), get_TrxName());	}

	/** Set Invoice.
		@param C_Invoice_ID 
		Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID)
	{
		if (C_Invoice_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Invoice_ID, Integer.valueOf(C_Invoice_ID));
	}

	/** Get Invoice.
		@return Invoice Identifier
	  */
	public int getC_Invoice_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Invoice_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException
    {
		return (org.compiere.model.I_C_Order)MTable.get(getCtx(), org.compiere.model.I_C_Order.Table_Name)
			.getPO(getC_Order_ID(), get_TrxName());	}

	/** Set Order.
		@param C_Order_ID 
		Order
	  */
	public void setC_Order_ID (int C_Order_ID)
	{
		if (C_Order_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_C_Order_ID, Integer.valueOf(C_Order_ID));
	}

	/** Get Order.
		@return Order
	  */
	public int getC_Order_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_C_Order_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Account Date.
		@param DateAcct 
		Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct)
	{
		set_ValueNoCheck (COLUMNNAME_DateAcct, DateAcct);
	}

	/** Get Account Date.
		@return Accounting Date
	  */
	public Timestamp getDateAcct () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateAcct);
	}

	/** Set Date Completed.
		@param DateCompleted Date Completed	  */
	public void setDateCompleted (Timestamp DateCompleted)
	{
		set_Value (COLUMNNAME_DateCompleted, DateCompleted);
	}

	/** Get Date Completed.
		@return Date Completed	  */
	public Timestamp getDateCompleted () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateCompleted);
	}

	/** Set Date Invoiced.
		@param DateInvoiced 
		Date printed on Invoice
	  */
	public void setDateInvoiced (Timestamp DateInvoiced)
	{
		set_ValueNoCheck (COLUMNNAME_DateInvoiced, DateInvoiced);
	}

	/** Get Date Invoiced.
		@return Date printed on Invoice
	  */
	public Timestamp getDateInvoiced () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateInvoiced);
	}

	/** Set Date Ordered.
		@param DateOrdered 
		Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered)
	{
		set_ValueNoCheck (COLUMNNAME_DateOrdered, DateOrdered);
	}

	/** Get Date Ordered.
		@return Date of Order
	  */
	public Timestamp getDateOrdered () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DateOrdered);
	}

	/** Set Date Prepared.
		@param DatePrepared Date Prepared	  */
	public void setDatePrepared (Timestamp DatePrepared)
	{
		set_Value (COLUMNNAME_DatePrepared, DatePrepared);
	}

	/** Get Date Prepared.
		@return Date Prepared	  */
	public Timestamp getDatePrepared () 
	{
		return (Timestamp)get_Value(COLUMNNAME_DatePrepared);
	}

	/** DocAction AD_Reference_ID=135 */
	public static final int DOCACTION_AD_Reference_ID=135;
	/** Complete = CO */
	public static final String DOCACTION_Complete = "CO";
	/** Approve = AP */
	public static final String DOCACTION_Approve = "AP";
	/** Reject = RJ */
	public static final String DOCACTION_Reject = "RJ";
	/** Post = PO */
	public static final String DOCACTION_Post = "PO";
	/** Void = VO */
	public static final String DOCACTION_Void = "VO";
	/** Close = CL */
	public static final String DOCACTION_Close = "CL";
	/** Reverse - Correct = RC */
	public static final String DOCACTION_Reverse_Correct = "RC";
	/** Reverse - Accrual = RA */
	public static final String DOCACTION_Reverse_Accrual = "RA";
	/** Invalidate = IN */
	public static final String DOCACTION_Invalidate = "IN";
	/** Re-activate = RE */
	public static final String DOCACTION_Re_Activate = "RE";
	/** <None> = -- */
	public static final String DOCACTION_None = "--";
	/** Prepare = PR */
	public static final String DOCACTION_Prepare = "PR";
	/** Unlock = XL */
	public static final String DOCACTION_Unlock = "XL";
	/** Wait Complete = WC */
	public static final String DOCACTION_WaitComplete = "WC";
	/** Set Document Action.
		@param DocAction 
		The targeted status of the document
	  */
	public void setDocAction (String DocAction)
	{

		set_Value (COLUMNNAME_DocAction, DocAction);
	}

	/** Get Document Action.
		@return The targeted status of the document
	  */
	public String getDocAction () 
	{
		return (String)get_Value(COLUMNNAME_DocAction);
	}

	/** DocStatus AD_Reference_ID=131 */
	public static final int DOCSTATUS_AD_Reference_ID=131;
	/** Drafted = DR */
	public static final String DOCSTATUS_Drafted = "DR";
	/** Completed = CO */
	public static final String DOCSTATUS_Completed = "CO";
	/** Approved = AP */
	public static final String DOCSTATUS_Approved = "AP";
	/** Not Approved = NA */
	public static final String DOCSTATUS_NotApproved = "NA";
	/** Voided = VO */
	public static final String DOCSTATUS_Voided = "VO";
	/** Invalid = IN */
	public static final String DOCSTATUS_Invalid = "IN";
	/** Reversed = RE */
	public static final String DOCSTATUS_Reversed = "RE";
	/** Closed = CL */
	public static final String DOCSTATUS_Closed = "CL";
	/** Unknown = ?? */
	public static final String DOCSTATUS_Unknown = "??";
	/** In Progress = IP */
	public static final String DOCSTATUS_InProgress = "IP";
	/** Waiting Payment = WP */
	public static final String DOCSTATUS_WaitingPayment = "WP";
	/** Waiting Confirmation = WC */
	public static final String DOCSTATUS_WaitingConfirmation = "WC";
	/** Set Document Status.
		@param DocStatus 
		The current status of the document
	  */
	public void setDocStatus (String DocStatus)
	{

		set_Value (COLUMNNAME_DocStatus, DocStatus);
	}

	/** Get Document Status.
		@return The current status of the document
	  */
	public String getDocStatus () 
	{
		return (String)get_Value(COLUMNNAME_DocStatus);
	}

	/** Set Document No.
		@param DocumentNo 
		Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo)
	{
		set_ValueNoCheck (COLUMNNAME_DocumentNo, DocumentNo);
	}

	/** Get Document No.
		@return Document sequence number of the document
	  */
	public String getDocumentNo () 
	{
		return (String)get_Value(COLUMNNAME_DocumentNo);
	}

	/** Set Approved.
		@param IsApproved 
		Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved)
	{
		set_ValueNoCheck (COLUMNNAME_IsApproved, Boolean.valueOf(IsApproved));
	}

	/** Get Approved.
		@return Indicates if this document requires approval
	  */
	public boolean isApproved () 
	{
		Object oo = get_Value(COLUMNNAME_IsApproved);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** No = N */
	public static final String ISFIXPRICE_No = "N";
	/** Si = Y */
	public static final String ISFIXPRICE_Si = "Y";
	/** Set Is Fix Price.
		@param IsFixPrice Is Fix Price	  */
	public void setIsFixPrice (String IsFixPrice)
	{

		set_Value (COLUMNNAME_IsFixPrice, IsFixPrice);
	}

	/** Get Is Fix Price.
		@return Is Fix Price	  */
	public String getIsFixPrice () 
	{
		return (String)get_Value(COLUMNNAME_IsFixPrice);
	}

	/** Set Is From Mass Update.
		@param IsFromMassUpdate Is From Mass Update	  */
	public void setIsFromMassUpdate (boolean IsFromMassUpdate)
	{
		set_Value (COLUMNNAME_IsFromMassUpdate, Boolean.valueOf(IsFromMassUpdate));
	}

	/** Get Is From Mass Update.
		@return Is From Mass Update	  */
	public boolean isFromMassUpdate () 
	{
		Object oo = get_Value(COLUMNNAME_IsFromMassUpdate);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Set Price Limit.
		@param IsSetPriceLimit Is Set Price Limit	  */
	public void setIsSetPriceLimit (boolean IsSetPriceLimit)
	{
		set_Value (COLUMNNAME_IsSetPriceLimit, Boolean.valueOf(IsSetPriceLimit));
	}

	/** Get Is Set Price Limit.
		@return Is Set Price Limit	  */
	public boolean isSetPriceLimit () 
	{
		Object oo = get_Value(COLUMNNAME_IsSetPriceLimit);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Set Price List.
		@param IsSetPriceList Is Set Price List	  */
	public void setIsSetPriceList (boolean IsSetPriceList)
	{
		set_Value (COLUMNNAME_IsSetPriceList, Boolean.valueOf(IsSetPriceList));
	}

	/** Get Is Set Price List.
		@return Is Set Price List	  */
	public boolean isSetPriceList () 
	{
		Object oo = get_Value(COLUMNNAME_IsSetPriceList);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Set Price Std.
		@param IsSetPriceStd Is Set Price Std	  */
	public void setIsSetPriceStd (boolean IsSetPriceStd)
	{
		set_Value (COLUMNNAME_IsSetPriceStd, Boolean.valueOf(IsSetPriceStd));
	}

	/** Get Is Set Price Std.
		@return Is Set Price Std	  */
	public boolean isSetPriceStd () 
	{
		Object oo = get_Value(COLUMNNAME_IsSetPriceStd);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Is Void Previous Documents.
		@param IsVoidPrevDocs Is Void Previous Documents	  */
	public void setIsVoidPrevDocs (boolean IsVoidPrevDocs)
	{
		set_Value (COLUMNNAME_IsVoidPrevDocs, Boolean.valueOf(IsVoidPrevDocs));
	}

	/** Get Is Void Previous Documents.
		@return Is Void Previous Documents	  */
	public boolean isVoidPrevDocs () 
	{
		Object oo = get_Value(COLUMNNAME_IsVoidPrevDocs);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Limit_Base AD_Reference_ID=194 */
	public static final int LIMIT_BASE_AD_Reference_ID=194;
	/** List Price = L */
	public static final String LIMIT_BASE_ListPrice = "L";
	/** Standard Price = S */
	public static final String LIMIT_BASE_StandardPrice = "S";
	/** Limit (PO) Price = X */
	public static final String LIMIT_BASE_LimitPOPrice = "X";
	/** Fixed Price = F */
	public static final String LIMIT_BASE_FixedPrice = "F";
	/** Product Cost = P */
	public static final String LIMIT_BASE_ProductCost = "P";
	/** Costs Structure = C */
	public static final String LIMIT_BASE_CostsStructure = "C";
	/** Last Purchase Order = O */
	public static final String LIMIT_BASE_LastPurchaseOrder = "O";
	/** Last Purchase Invoice = I */
	public static final String LIMIT_BASE_LastPurchaseInvoice = "I";
	/** Higher Invoice Price = H */
	public static final String LIMIT_BASE_HigherInvoicePrice = "H";
	/** Set Limit price Base.
		@param Limit_Base 
		Base price for calculation of the new price
	  */
	public void setLimit_Base (String Limit_Base)
	{

		set_Value (COLUMNNAME_Limit_Base, Limit_Base);
	}

	/** Get Limit price Base.
		@return Base price for calculation of the new price
	  */
	public String getLimit_Base () 
	{
		return (String)get_Value(COLUMNNAME_Limit_Base);
	}

	/** List_Base AD_Reference_ID=194 */
	public static final int LIST_BASE_AD_Reference_ID=194;
	/** List Price = L */
	public static final String LIST_BASE_ListPrice = "L";
	/** Standard Price = S */
	public static final String LIST_BASE_StandardPrice = "S";
	/** Limit (PO) Price = X */
	public static final String LIST_BASE_LimitPOPrice = "X";
	/** Fixed Price = F */
	public static final String LIST_BASE_FixedPrice = "F";
	/** Product Cost = P */
	public static final String LIST_BASE_ProductCost = "P";
	/** Costs Structure = C */
	public static final String LIST_BASE_CostsStructure = "C";
	/** Last Purchase Order = O */
	public static final String LIST_BASE_LastPurchaseOrder = "O";
	/** Last Purchase Invoice = I */
	public static final String LIST_BASE_LastPurchaseInvoice = "I";
	/** Higher Invoice Price = H */
	public static final String LIST_BASE_HigherInvoicePrice = "H";
	/** Set List price Base.
		@param List_Base 
		Price used as the basis for price list calculations
	  */
	public void setList_Base (String List_Base)
	{

		set_Value (COLUMNNAME_List_Base, List_Base);
	}

	/** Get List price Base.
		@return Price used as the basis for price list calculations
	  */
	public String getList_Base () 
	{
		return (String)get_Value(COLUMNNAME_List_Base);
	}

	public org.compiere.model.I_M_InOut getM_InOut() throws RuntimeException
    {
		return (org.compiere.model.I_M_InOut)MTable.get(getCtx(), org.compiere.model.I_M_InOut.Table_Name)
			.getPO(getM_InOut_ID(), get_TrxName());	}

	/** Set Shipment/Receipt.
		@param M_InOut_ID 
		Material Shipment Document
	  */
	public void setM_InOut_ID (int M_InOut_ID)
	{
		if (M_InOut_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_InOut_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_InOut_ID, Integer.valueOf(M_InOut_ID));
	}

	/** Get Shipment/Receipt.
		@return Material Shipment Document
	  */
	public int getM_InOut_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_InOut_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_MovementConfirm getM_MovementConfirm() throws RuntimeException
    {
		return (org.compiere.model.I_M_MovementConfirm)MTable.get(getCtx(), org.compiere.model.I_M_MovementConfirm.Table_Name)
			.getPO(getM_MovementConfirm_ID(), get_TrxName());	}

	/** Set Move Confirm.
		@param M_MovementConfirm_ID 
		Inventory Move Confirmation
	  */
	public void setM_MovementConfirm_ID (int M_MovementConfirm_ID)
	{
		if (M_MovementConfirm_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_MovementConfirm_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_MovementConfirm_ID, Integer.valueOf(M_MovementConfirm_ID));
	}

	/** Get Move Confirm.
		@return Inventory Move Confirmation
	  */
	public int getM_MovementConfirm_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_MovementConfirm_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Movement Date.
		@param MovementDate 
		Date a product was moved in or out of inventory
	  */
	public void setMovementDate (Timestamp MovementDate)
	{
		set_Value (COLUMNNAME_MovementDate, MovementDate);
	}

	/** Get Movement Date.
		@return Date a product was moved in or out of inventory
	  */
	public Timestamp getMovementDate () 
	{
		return (Timestamp)get_Value(COLUMNNAME_MovementDate);
	}

	public org.compiere.model.I_M_PriceList_Version getM_PriceList_Version() throws RuntimeException
    {
		return (org.compiere.model.I_M_PriceList_Version)MTable.get(getCtx(), org.compiere.model.I_M_PriceList_Version.Table_Name)
			.getPO(getM_PriceList_Version_ID(), get_TrxName());	}

	/** Set Price List Version.
		@param M_PriceList_Version_ID 
		Identifies a unique instance of a Price List
	  */
	public void setM_PriceList_Version_ID (int M_PriceList_Version_ID)
	{
		if (M_PriceList_Version_ID < 1) 
			set_Value (COLUMNNAME_M_PriceList_Version_ID, null);
		else 
			set_Value (COLUMNNAME_M_PriceList_Version_ID, Integer.valueOf(M_PriceList_Version_ID));
	}

	/** Get Price List Version.
		@return Identifies a unique instance of a Price List
	  */
	public int getM_PriceList_Version_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_PriceList_Version_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException
    {
		return (org.compiere.model.I_M_Product)MTable.get(getCtx(), org.compiere.model.I_M_Product.Table_Name)
			.getPO(getM_Product_ID(), get_TrxName());	}

	/** Set Product.
		@param M_Product_ID 
		Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID)
	{
		if (M_Product_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
	}

	/** Get Product.
		@return Product, Service, Item
	  */
	public int getM_Product_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Product_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_M_Production getM_Production() throws RuntimeException
    {
		return (org.compiere.model.I_M_Production)MTable.get(getCtx(), org.compiere.model.I_M_Production.Table_Name)
			.getPO(getM_Production_ID(), get_TrxName());	}

	/** Set Production.
		@param M_Production_ID 
		Plan for producing a product
	  */
	public void setM_Production_ID (int M_Production_ID)
	{
		if (M_Production_ID < 1) 
			set_Value (COLUMNNAME_M_Production_ID, null);
		else 
			set_Value (COLUMNNAME_M_Production_ID, Integer.valueOf(M_Production_ID));
	}

	/** Get Production.
		@return Plan for producing a product
	  */
	public int getM_Production_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_M_Production_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Percentage Profit Price Limit.
		@param PercentageProfitPLimit Percentage Profit Price Limit	  */
	public void setPercentageProfitPLimit (BigDecimal PercentageProfitPLimit)
	{
		set_Value (COLUMNNAME_PercentageProfitPLimit, PercentageProfitPLimit);
	}

	/** Get Percentage Profit Price Limit.
		@return Percentage Profit Price Limit	  */
	public BigDecimal getPercentageProfitPLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PercentageProfitPLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Percentage Profit Price List.
		@param PercentageProfitPList Percentage Profit Price List	  */
	public void setPercentageProfitPList (BigDecimal PercentageProfitPList)
	{
		set_Value (COLUMNNAME_PercentageProfitPList, PercentageProfitPList);
	}

	/** Get Percentage Profit Price List.
		@return Percentage Profit Price List	  */
	public BigDecimal getPercentageProfitPList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PercentageProfitPList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Percentage Profit Price Standart.
		@param PercentageProfitPStd Percentage Profit Price Standart	  */
	public void setPercentageProfitPStd (BigDecimal PercentageProfitPStd)
	{
		set_Value (COLUMNNAME_PercentageProfitPStd, PercentageProfitPStd);
	}

	/** Get Percentage Profit Price Standart.
		@return Percentage Profit Price Standart	  */
	public BigDecimal getPercentageProfitPStd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PercentageProfitPStd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Unit Price.
		@param PriceActual 
		Actual Price 
	  */
	public void setPriceActual (BigDecimal PriceActual)
	{
		set_ValueNoCheck (COLUMNNAME_PriceActual, PriceActual);
	}

	/** Get Unit Price.
		@return Actual Price 
	  */
	public BigDecimal getPriceActual () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceActual);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Last Invoice Price.
		@param PriceLastInv 
		Price of the last invoice for the product
	  */
	public void setPriceLastInv (BigDecimal PriceLastInv)
	{
		set_Value (COLUMNNAME_PriceLastInv, PriceLastInv);
	}

	/** Get Last Invoice Price.
		@return Price of the last invoice for the product
	  */
	public BigDecimal getPriceLastInv () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLastInv);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price Last Order.
		@param PriceLastOrd Price Last Order	  */
	public void setPriceLastOrd (BigDecimal PriceLastOrd)
	{
		set_ValueNoCheck (COLUMNNAME_PriceLastOrd, PriceLastOrd);
	}

	/** Get Price Last Order.
		@return Price Last Order	  */
	public BigDecimal getPriceLastOrd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLastOrd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceLastProduction.
		@param PriceLastProduction PriceLastProduction	  */
	public void setPriceLastProduction (BigDecimal PriceLastProduction)
	{
		set_Value (COLUMNNAME_PriceLastProduction, PriceLastProduction);
	}

	/** Get PriceLastProduction.
		@return PriceLastProduction	  */
	public BigDecimal getPriceLastProduction () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLastProduction);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Limit Price.
		@param PriceLimit 
		Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit)
	{
		set_Value (COLUMNNAME_PriceLimit, PriceLimit);
	}

	/** Get Limit Price.
		@return Lowest price for a product
	  */
	public BigDecimal getPriceLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Limit Price.
		@param PriceLimitEntered 
		Entered Limit Price
	  */
	public void setPriceLimitEntered (BigDecimal PriceLimitEntered)
	{
		set_Value (COLUMNNAME_PriceLimitEntered, PriceLimitEntered);
	}

	/** Get Limit Price.
		@return Entered Limit Price
	  */
	public BigDecimal getPriceLimitEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimitEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Limit Price Old.
		@param PriceLimitOld 
		Old Limit Price
	  */
	public void setPriceLimitOld (BigDecimal PriceLimitOld)
	{
		set_Value (COLUMNNAME_PriceLimitOld, PriceLimitOld);
	}

	/** Get Limit Price Old.
		@return Old Limit Price
	  */
	public BigDecimal getPriceLimitOld () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimitOld);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price Limit With Tax.
		@param PriceLimitWTax Price Limit With Tax	  */
	public void setPriceLimitWTax (BigDecimal PriceLimitWTax)
	{
		set_ValueNoCheck (COLUMNNAME_PriceLimitWTax, PriceLimitWTax);
	}

	/** Get Price Limit With Tax.
		@return Price Limit With Tax	  */
	public BigDecimal getPriceLimitWTax () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceLimitWTax);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price.
		@param PriceList 
		List Price
	  */
	public void setPriceList (BigDecimal PriceList)
	{
		set_Value (COLUMNNAME_PriceList, PriceList);
	}

	/** Get List Price.
		@return List Price
	  */
	public BigDecimal getPriceList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price Entered.
		@param PriceListEntered 
		Entered List Price
	  */
	public void setPriceListEntered (BigDecimal PriceListEntered)
	{
		set_Value (COLUMNNAME_PriceListEntered, PriceListEntered);
	}

	/** Get List Price Entered.
		@return Entered List Price
	  */
	public BigDecimal getPriceListEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceListEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set List Price Old.
		@param PriceListOld 
		Old List Price
	  */
	public void setPriceListOld (BigDecimal PriceListOld)
	{
		set_Value (COLUMNNAME_PriceListOld, PriceListOld);
	}

	/** Get List Price Old.
		@return Old List Price
	  */
	public BigDecimal getPriceListOld () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceListOld);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set PriceList With Tax.
		@param PriceListWTax PriceList With Tax	  */
	public void setPriceListWTax (BigDecimal PriceListWTax)
	{
		set_ValueNoCheck (COLUMNNAME_PriceListWTax, PriceListWTax);
	}

	/** Get PriceList With Tax.
		@return PriceList With Tax	  */
	public BigDecimal getPriceListWTax () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceListWTax);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Standard Price.
		@param PriceStd 
		Standard Price
	  */
	public void setPriceStd (BigDecimal PriceStd)
	{
		set_Value (COLUMNNAME_PriceStd, PriceStd);
	}

	/** Get Standard Price.
		@return Standard Price
	  */
	public BigDecimal getPriceStd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceStd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Standart Price.
		@param PriceStdEntered 
		Entered Standart Price
	  */
	public void setPriceStdEntered (BigDecimal PriceStdEntered)
	{
		set_Value (COLUMNNAME_PriceStdEntered, PriceStdEntered);
	}

	/** Get Standart Price.
		@return Entered Standart Price
	  */
	public BigDecimal getPriceStdEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceStdEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Standart Price Old.
		@param PriceStdOld 
		Old Standart Price
	  */
	public void setPriceStdOld (BigDecimal PriceStdOld)
	{
		set_Value (COLUMNNAME_PriceStdOld, PriceStdOld);
	}

	/** Get Standart Price Old.
		@return Old Standart Price
	  */
	public BigDecimal getPriceStdOld () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceStdOld);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Price Standard With Tax.
		@param PriceStdWTax Price Standard With Tax	  */
	public void setPriceStdWTax (BigDecimal PriceStdWTax)
	{
		set_ValueNoCheck (COLUMNNAME_PriceStdWTax, PriceStdWTax);
	}

	/** Get Price Standard With Tax.
		@return Price Standard With Tax	  */
	public BigDecimal getPriceStdWTax () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PriceStdWTax);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Processed.
		@param Processed 
		The document has been processed
	  */
	public void setProcessed (boolean Processed)
	{
		set_Value (COLUMNNAME_Processed, Boolean.valueOf(Processed));
	}

	/** Get Processed.
		@return The document has been processed
	  */
	public boolean isProcessed () 
	{
		Object oo = get_Value(COLUMNNAME_Processed);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Processed On.
		@param ProcessedOn 
		The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn)
	{
		set_Value (COLUMNNAME_ProcessedOn, ProcessedOn);
	}

	/** Get Processed On.
		@return The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_ProcessedOn);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Process Now.
		@param Processing Process Now	  */
	public void setProcessing (boolean Processing)
	{
		set_Value (COLUMNNAME_Processing, Boolean.valueOf(Processing));
	}

	/** Get Process Now.
		@return Process Now	  */
	public boolean isProcessing () 
	{
		Object oo = get_Value(COLUMNNAME_Processing);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Product.
		@param Product Product	  */
	public void setProduct (String Product)
	{
		throw new IllegalArgumentException ("Product is virtual column");	}

	/** Get Product.
		@return Product	  */
	public String getProduct () 
	{
		return (String)get_Value(COLUMNNAME_Product);
	}

	/** Std_Base AD_Reference_ID=194 */
	public static final int STD_BASE_AD_Reference_ID=194;
	/** List Price = L */
	public static final String STD_BASE_ListPrice = "L";
	/** Standard Price = S */
	public static final String STD_BASE_StandardPrice = "S";
	/** Limit (PO) Price = X */
	public static final String STD_BASE_LimitPOPrice = "X";
	/** Fixed Price = F */
	public static final String STD_BASE_FixedPrice = "F";
	/** Product Cost = P */
	public static final String STD_BASE_ProductCost = "P";
	/** Costs Structure = C */
	public static final String STD_BASE_CostsStructure = "C";
	/** Last Purchase Order = O */
	public static final String STD_BASE_LastPurchaseOrder = "O";
	/** Last Purchase Invoice = I */
	public static final String STD_BASE_LastPurchaseInvoice = "I";
	/** Higher Invoice Price = H */
	public static final String STD_BASE_HigherInvoicePrice = "H";
	/** Set Standard price Base.
		@param Std_Base 
		Base price for calculating new standard price
	  */
	public void setStd_Base (String Std_Base)
	{

		set_Value (COLUMNNAME_Std_Base, Std_Base);
	}

	/** Get Standard price Base.
		@return Base price for calculating new standard price
	  */
	public String getStd_Base () 
	{
		return (String)get_Value(COLUMNNAME_Std_Base);
	}

	/** Set Tax Amt Price Limit.
		@param TaxAmtPriceLimit Tax Amt Price Limit	  */
	public void setTaxAmtPriceLimit (BigDecimal TaxAmtPriceLimit)
	{
		set_ValueNoCheck (COLUMNNAME_TaxAmtPriceLimit, TaxAmtPriceLimit);
	}

	/** Get Tax Amt Price Limit.
		@return Tax Amt Price Limit	  */
	public BigDecimal getTaxAmtPriceLimit () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxAmtPriceLimit);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Tax Amt Price List.
		@param TaxAmtPriceList Tax Amt Price List	  */
	public void setTaxAmtPriceList (BigDecimal TaxAmtPriceList)
	{
		set_ValueNoCheck (COLUMNNAME_TaxAmtPriceList, TaxAmtPriceList);
	}

	/** Get Tax Amt Price List.
		@return Tax Amt Price List	  */
	public BigDecimal getTaxAmtPriceList () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxAmtPriceList);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Tax Amt Price Standard.
		@param TaxAmtPriceStd Tax Amt Price Standard	  */
	public void setTaxAmtPriceStd (BigDecimal TaxAmtPriceStd)
	{
		set_ValueNoCheck (COLUMNNAME_TaxAmtPriceStd, TaxAmtPriceStd);
	}

	/** Get Tax Amt Price Standard.
		@return Tax Amt Price Standard	  */
	public BigDecimal getTaxAmtPriceStd () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_TaxAmtPriceStd);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1_ID(), get_TrxName());	}

	/** Set User Element List 1.
		@param User1_ID 
		User defined list element #1
	  */
	public void setUser1_ID (int User1_ID)
	{
		if (User1_ID < 1) 
			set_Value (COLUMNNAME_User1_ID, null);
		else 
			set_Value (COLUMNNAME_User1_ID, Integer.valueOf(User1_ID));
	}

	/** Get User Element List 1.
		@return User defined list element #1
	  */
	public int getUser1_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser1W() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1W_ID(), get_TrxName());	}

	/** Set User Element List 1 W.
		@param User1W_ID 
		User defined list element #1
	  */
	public void setUser1W_ID (int User1W_ID)
	{
		if (User1W_ID < 1) 
			set_Value (COLUMNNAME_User1W_ID, null);
		else 
			set_Value (COLUMNNAME_User1W_ID, Integer.valueOf(User1W_ID));
	}

	/** Get User Element List 1 W.
		@return User defined list element #1
	  */
	public int getUser1W_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1W_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser1X() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1X_ID(), get_TrxName());	}

	/** Set User Element List 1 X.
		@param User1X_ID 
		User defined list element #1
	  */
	public void setUser1X_ID (int User1X_ID)
	{
		if (User1X_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_User1X_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_User1X_ID, Integer.valueOf(User1X_ID));
	}

	/** Get User Element List 1 X.
		@return User defined list element #1
	  */
	public int getUser1X_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1X_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser1Y() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1Y_ID(), get_TrxName());	}

	/** Set User Element List 1 Y.
		@param User1Y_ID 
		User defined list element #1
	  */
	public void setUser1Y_ID (int User1Y_ID)
	{
		if (User1Y_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_User1Y_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_User1Y_ID, Integer.valueOf(User1Y_ID));
	}

	/** Get User Element List 1 Y.
		@return User defined list element #1
	  */
	public int getUser1Y_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1Y_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	public org.compiere.model.I_C_ElementValue getUser1Z() throws RuntimeException
    {
		return (org.compiere.model.I_C_ElementValue)MTable.get(getCtx(), org.compiere.model.I_C_ElementValue.Table_Name)
			.getPO(getUser1Z_ID(), get_TrxName());	}

	/** Set User Element List 1 Z.
		@param User1Z_ID 
		User defined list element #1
	  */
	public void setUser1Z_ID (int User1Z_ID)
	{
		if (User1Z_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_User1Z_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_User1Z_ID, Integer.valueOf(User1Z_ID));
	}

	/** Get User Element List 1 Z.
		@return User defined list element #1
	  */
	public int getUser1Z_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_User1Z_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}
}