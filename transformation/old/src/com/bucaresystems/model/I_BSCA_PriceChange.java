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
package com.bucaresystems.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.compiere.model.*;
import org.compiere.util.KeyNamePair;

/** Generated Interface for BSCA_PriceChange
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_BSCA_PriceChange 
{

    /** TableName=BSCA_PriceChange */
    public static final String Table_Name = "BSCA_PriceChange";

    /** AD_Table_ID=1000276 */
    public static final int Table_ID = MTable.getTable_ID(Table_Name);

    KeyNamePair Model = new KeyNamePair(Table_ID, Table_Name);

    /** AccessLevel = 3 - Client - Org 
     */
    BigDecimal accessLevel = BigDecimal.valueOf(3);

    /** Load Meta Data */

    /** Column name AD_Client_ID */
    public static final String COLUMNNAME_AD_Client_ID = "AD_Client_ID";

	/** Get Client.
	  * Client/Tenant for this installation.
	  */
	public int getAD_Client_ID();

    /** Column name AD_Org_ID */
    public static final String COLUMNNAME_AD_Org_ID = "AD_Org_ID";

	/** Set Organization.
	  * Organizational entity within client
	  */
	public void setAD_Org_ID (int AD_Org_ID);

	/** Get Organization.
	  * Organizational entity within client
	  */
	public int getAD_Org_ID();

    /** Column name AD_OrgTrx_ID */
    public static final String COLUMNNAME_AD_OrgTrx_ID = "AD_OrgTrx_ID";

	/** Set Trx Organization.
	  * Performing or initiating organization
	  */
	public void setAD_OrgTrx_ID (int AD_OrgTrx_ID);

	/** Get Trx Organization.
	  * Performing or initiating organization
	  */
	public int getAD_OrgTrx_ID();

    /** Column name BSCA_CompletedBy_ID */
    public static final String COLUMNNAME_BSCA_CompletedBy_ID = "BSCA_CompletedBy_ID";

	/** Set BSCA_CompletedBy_ID	  */
	public void setBSCA_CompletedBy_ID (int BSCA_CompletedBy_ID);

	/** Get BSCA_CompletedBy_ID	  */
	public int getBSCA_CompletedBy_ID();

	public org.compiere.model.I_AD_User getBSCA_CompletedBy() throws RuntimeException;

    /** Column name BSCA_PreparedBy_ID */
    public static final String COLUMNNAME_BSCA_PreparedBy_ID = "BSCA_PreparedBy_ID";

	/** Set BSCA_PreparedBy_ID	  */
	public void setBSCA_PreparedBy_ID (int BSCA_PreparedBy_ID);

	/** Get BSCA_PreparedBy_ID	  */
	public int getBSCA_PreparedBy_ID();

	public org.compiere.model.I_AD_User getBSCA_PreparedBy() throws RuntimeException;

    /** Column name BSCA_PriceChange_ID */
    public static final String COLUMNNAME_BSCA_PriceChange_ID = "BSCA_PriceChange_ID";

	/** Set Price Change	  */
	public void setBSCA_PriceChange_ID (int BSCA_PriceChange_ID);

	/** Get Price Change	  */
	public int getBSCA_PriceChange_ID();

    /** Column name BSCA_PriceChangePrev_ID */
    public static final String COLUMNNAME_BSCA_PriceChangePrev_ID = "BSCA_PriceChangePrev_ID";

	/** Set Price Change Previus	  */
	public void setBSCA_PriceChangePrev_ID (int BSCA_PriceChangePrev_ID);

	/** Get Price Change Previus	  */
	public int getBSCA_PriceChangePrev_ID();

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PriceChangePrev() throws RuntimeException;

    /** Column name BSCA_PriceChange_UU */
    public static final String COLUMNNAME_BSCA_PriceChange_UU = "BSCA_PriceChange_UU";

	/** Set BSCA_PriceChange_UU	  */
	public void setBSCA_PriceChange_UU (String BSCA_PriceChange_UU);

	/** Get BSCA_PriceChange_UU	  */
	public String getBSCA_PriceChange_UU();

    /** Column name BSCA_PrintPrice */
    public static final String COLUMNNAME_BSCA_PrintPrice = "BSCA_PrintPrice";

	/** Set Print Price	  */
	public void setBSCA_PrintPrice (String BSCA_PrintPrice);

	/** Get Print Price	  */
	public String getBSCA_PrintPrice();

    /** Column name BSCA_PrintPrice2 */
    public static final String COLUMNNAME_BSCA_PrintPrice2 = "BSCA_PrintPrice2";

	/** Set Print Price 2	  */
	public void setBSCA_PrintPrice2 (String BSCA_PrintPrice2);

	/** Get Print Price 2	  */
	public String getBSCA_PrintPrice2();

    /** Column name BSCA_ProductValue_ID */
    public static final String COLUMNNAME_BSCA_ProductValue_ID = "BSCA_ProductValue_ID";

	/** Set BSCA_ProductValue	  */
	public void setBSCA_ProductValue_ID (int BSCA_ProductValue_ID);

	/** Get BSCA_ProductValue	  */
	public int getBSCA_ProductValue_ID();

	public com.bucaresystems.model.I_BSCA_ProductValue getBSCA_ProductValue() throws RuntimeException;

    /** Column name BSCA_ProfitPriceLimit */
    public static final String COLUMNNAME_BSCA_ProfitPriceLimit = "BSCA_ProfitPriceLimit";

	/** Set Profit Price Limit	  */
	public void setBSCA_ProfitPriceLimit (BigDecimal BSCA_ProfitPriceLimit);

	/** Get Profit Price Limit	  */
	public BigDecimal getBSCA_ProfitPriceLimit();

    /** Column name BSCA_ProfitPriceLimitEntered */
    public static final String COLUMNNAME_BSCA_ProfitPriceLimitEntered = "BSCA_ProfitPriceLimitEntered";

	/** Set Profit Price Limit Entered	  */
	public void setBSCA_ProfitPriceLimitEntered (BigDecimal BSCA_ProfitPriceLimitEntered);

	/** Get Profit Price Limit Entered	  */
	public BigDecimal getBSCA_ProfitPriceLimitEntered();

    /** Column name BSCA_ProfitPriceList */
    public static final String COLUMNNAME_BSCA_ProfitPriceList = "BSCA_ProfitPriceList";

	/** Set Profit Price List	  */
	public void setBSCA_ProfitPriceList (BigDecimal BSCA_ProfitPriceList);

	/** Get Profit Price List	  */
	public BigDecimal getBSCA_ProfitPriceList();

    /** Column name BSCA_ProfitPriceListEntered */
    public static final String COLUMNNAME_BSCA_ProfitPriceListEntered = "BSCA_ProfitPriceListEntered";

	/** Set Profit Price List Entered	  */
	public void setBSCA_ProfitPriceListEntered (BigDecimal BSCA_ProfitPriceListEntered);

	/** Get Profit Price List Entered	  */
	public BigDecimal getBSCA_ProfitPriceListEntered();

    /** Column name BSCA_ProfitPriceStd */
    public static final String COLUMNNAME_BSCA_ProfitPriceStd = "BSCA_ProfitPriceStd";

	/** Set Profit Price Std	  */
	public void setBSCA_ProfitPriceStd (BigDecimal BSCA_ProfitPriceStd);

	/** Get Profit Price Std	  */
	public BigDecimal getBSCA_ProfitPriceStd();

    /** Column name BSCA_ProfitPriceStdEntered */
    public static final String COLUMNNAME_BSCA_ProfitPriceStdEntered = "BSCA_ProfitPriceStdEntered";

	/** Set Profit Price Std Entered	  */
	public void setBSCA_ProfitPriceStdEntered (BigDecimal BSCA_ProfitPriceStdEntered);

	/** Get Profit Price Std Entered	  */
	public BigDecimal getBSCA_ProfitPriceStdEntered();

    /** Column name BSCA_QtyCurrent */
    public static final String COLUMNNAME_BSCA_QtyCurrent = "BSCA_QtyCurrent";

	/** Set Qty Current	  */
	public void setBSCA_QtyCurrent (BigDecimal BSCA_QtyCurrent);

	/** Get Qty Current	  */
	public BigDecimal getBSCA_QtyCurrent();

    /** Column name BSCA_SendTxtScale */
    public static final String COLUMNNAME_BSCA_SendTxtScale = "BSCA_SendTxtScale";

	/** Set Send Txt Scale	  */
	public void setBSCA_SendTxtScale (String BSCA_SendTxtScale);

	/** Get Send Txt Scale	  */
	public String getBSCA_SendTxtScale();

    /** Column name C_DocType_ID */
    public static final String COLUMNNAME_C_DocType_ID = "C_DocType_ID";

	/** Set Document Type.
	  * Document type or rules
	  */
	public void setC_DocType_ID (int C_DocType_ID);

	/** Get Document Type.
	  * Document type or rules
	  */
	public int getC_DocType_ID();

	public org.compiere.model.I_C_DocType getC_DocType() throws RuntimeException;

    /** Column name C_DocTypeTarget_ID */
    public static final String COLUMNNAME_C_DocTypeTarget_ID = "C_DocTypeTarget_ID";

	/** Set Target Document Type.
	  * Target document type for conversing documents
	  */
	public void setC_DocTypeTarget_ID (int C_DocTypeTarget_ID);

	/** Get Target Document Type.
	  * Target document type for conversing documents
	  */
	public int getC_DocTypeTarget_ID();

	public org.compiere.model.I_C_DocType getC_DocTypeTarget() throws RuntimeException;

    /** Column name C_Invoice_ID */
    public static final String COLUMNNAME_C_Invoice_ID = "C_Invoice_ID";

	/** Set Invoice.
	  * Invoice Identifier
	  */
	public void setC_Invoice_ID (int C_Invoice_ID);

	/** Get Invoice.
	  * Invoice Identifier
	  */
	public int getC_Invoice_ID();

	public org.compiere.model.I_C_Invoice getC_Invoice() throws RuntimeException;

    /** Column name C_Order_ID */
    public static final String COLUMNNAME_C_Order_ID = "C_Order_ID";

	/** Set Order.
	  * Order
	  */
	public void setC_Order_ID (int C_Order_ID);

	/** Get Order.
	  * Order
	  */
	public int getC_Order_ID();

	public org.compiere.model.I_C_Order getC_Order() throws RuntimeException;

    /** Column name Created */
    public static final String COLUMNNAME_Created = "Created";

	/** Get Created.
	  * Date this record was created
	  */
	public Timestamp getCreated();

    /** Column name CreatedBy */
    public static final String COLUMNNAME_CreatedBy = "CreatedBy";

	/** Get Created By.
	  * User who created this records
	  */
	public int getCreatedBy();

    /** Column name DateAcct */
    public static final String COLUMNNAME_DateAcct = "DateAcct";

	/** Set Account Date.
	  * Accounting Date
	  */
	public void setDateAcct (Timestamp DateAcct);

	/** Get Account Date.
	  * Accounting Date
	  */
	public Timestamp getDateAcct();

    /** Column name DateCompleted */
    public static final String COLUMNNAME_DateCompleted = "DateCompleted";

	/** Set Date Completed	  */
	public void setDateCompleted (Timestamp DateCompleted);

	/** Get Date Completed	  */
	public Timestamp getDateCompleted();

    /** Column name DateInvoiced */
    public static final String COLUMNNAME_DateInvoiced = "DateInvoiced";

	/** Set Date Invoiced.
	  * Date printed on Invoice
	  */
	public void setDateInvoiced (Timestamp DateInvoiced);

	/** Get Date Invoiced.
	  * Date printed on Invoice
	  */
	public Timestamp getDateInvoiced();

    /** Column name DateOrdered */
    public static final String COLUMNNAME_DateOrdered = "DateOrdered";

	/** Set Date Ordered.
	  * Date of Order
	  */
	public void setDateOrdered (Timestamp DateOrdered);

	/** Get Date Ordered.
	  * Date of Order
	  */
	public Timestamp getDateOrdered();

    /** Column name DatePrepared */
    public static final String COLUMNNAME_DatePrepared = "DatePrepared";

	/** Set Date Prepared	  */
	public void setDatePrepared (Timestamp DatePrepared);

	/** Get Date Prepared	  */
	public Timestamp getDatePrepared();

    /** Column name DocAction */
    public static final String COLUMNNAME_DocAction = "DocAction";

	/** Set Document Action.
	  * The targeted status of the document
	  */
	public void setDocAction (String DocAction);

	/** Get Document Action.
	  * The targeted status of the document
	  */
	public String getDocAction();

    /** Column name DocStatus */
    public static final String COLUMNNAME_DocStatus = "DocStatus";

	/** Set Document Status.
	  * The current status of the document
	  */
	public void setDocStatus (String DocStatus);

	/** Get Document Status.
	  * The current status of the document
	  */
	public String getDocStatus();

    /** Column name DocumentNo */
    public static final String COLUMNNAME_DocumentNo = "DocumentNo";

	/** Set Document No.
	  * Document sequence number of the document
	  */
	public void setDocumentNo (String DocumentNo);

	/** Get Document No.
	  * Document sequence number of the document
	  */
	public String getDocumentNo();

    /** Column name IsActive */
    public static final String COLUMNNAME_IsActive = "IsActive";

	/** Set Active.
	  * The record is active in the system
	  */
	public void setIsActive (boolean IsActive);

	/** Get Active.
	  * The record is active in the system
	  */
	public boolean isActive();

    /** Column name IsApproved */
    public static final String COLUMNNAME_IsApproved = "IsApproved";

	/** Set Approved.
	  * Indicates if this document requires approval
	  */
	public void setIsApproved (boolean IsApproved);

	/** Get Approved.
	  * Indicates if this document requires approval
	  */
	public boolean isApproved();

    /** Column name IsFixPrice */
    public static final String COLUMNNAME_IsFixPrice = "IsFixPrice";

	/** Set Is Fix Price	  */
	public void setIsFixPrice (String IsFixPrice);

	/** Get Is Fix Price	  */
	public String getIsFixPrice();

    /** Column name IsFromMassUpdate */
    public static final String COLUMNNAME_IsFromMassUpdate = "IsFromMassUpdate";

	/** Set Is From Mass Update	  */
	public void setIsFromMassUpdate (boolean IsFromMassUpdate);

	/** Get Is From Mass Update	  */
	public boolean isFromMassUpdate();

    /** Column name IsSetPriceLimit */
    public static final String COLUMNNAME_IsSetPriceLimit = "IsSetPriceLimit";

	/** Set Is Set Price Limit	  */
	public void setIsSetPriceLimit (boolean IsSetPriceLimit);

	/** Get Is Set Price Limit	  */
	public boolean isSetPriceLimit();

    /** Column name IsSetPriceList */
    public static final String COLUMNNAME_IsSetPriceList = "IsSetPriceList";

	/** Set Is Set Price List	  */
	public void setIsSetPriceList (boolean IsSetPriceList);

	/** Get Is Set Price List	  */
	public boolean isSetPriceList();

    /** Column name IsSetPriceStd */
    public static final String COLUMNNAME_IsSetPriceStd = "IsSetPriceStd";

	/** Set Is Set Price Std	  */
	public void setIsSetPriceStd (boolean IsSetPriceStd);

	/** Get Is Set Price Std	  */
	public boolean isSetPriceStd();

    /** Column name IsVoidPrevDocs */
    public static final String COLUMNNAME_IsVoidPrevDocs = "IsVoidPrevDocs";

	/** Set Is Void Previous Documents	  */
	public void setIsVoidPrevDocs (boolean IsVoidPrevDocs);

	/** Get Is Void Previous Documents	  */
	public boolean isVoidPrevDocs();

    /** Column name Limit_Base */
    public static final String COLUMNNAME_Limit_Base = "Limit_Base";

	/** Set Limit price Base.
	  * Base price for calculation of the new price
	  */
	public void setLimit_Base (String Limit_Base);

	/** Get Limit price Base.
	  * Base price for calculation of the new price
	  */
	public String getLimit_Base();

    /** Column name List_Base */
    public static final String COLUMNNAME_List_Base = "List_Base";

	/** Set List price Base.
	  * Price used as the basis for price list calculations
	  */
	public void setList_Base (String List_Base);

	/** Get List price Base.
	  * Price used as the basis for price list calculations
	  */
	public String getList_Base();

    /** Column name M_InOut_ID */
    public static final String COLUMNNAME_M_InOut_ID = "M_InOut_ID";

	/** Set Shipment/Receipt.
	  * Material Shipment Document
	  */
	public void setM_InOut_ID (int M_InOut_ID);

	/** Get Shipment/Receipt.
	  * Material Shipment Document
	  */
	public int getM_InOut_ID();

	public org.compiere.model.I_M_InOut getM_InOut() throws RuntimeException;

    /** Column name M_MovementConfirm_ID */
    public static final String COLUMNNAME_M_MovementConfirm_ID = "M_MovementConfirm_ID";

	/** Set Move Confirm.
	  * Inventory Move Confirmation
	  */
	public void setM_MovementConfirm_ID (int M_MovementConfirm_ID);

	/** Get Move Confirm.
	  * Inventory Move Confirmation
	  */
	public int getM_MovementConfirm_ID();

	public org.compiere.model.I_M_MovementConfirm getM_MovementConfirm() throws RuntimeException;

    /** Column name MovementDate */
    public static final String COLUMNNAME_MovementDate = "MovementDate";

	/** Set Movement Date.
	  * Date a product was moved in or out of inventory
	  */
	public void setMovementDate (Timestamp MovementDate);

	/** Get Movement Date.
	  * Date a product was moved in or out of inventory
	  */
	public Timestamp getMovementDate();

    /** Column name M_PriceList_Version_ID */
    public static final String COLUMNNAME_M_PriceList_Version_ID = "M_PriceList_Version_ID";

	/** Set Price List Version.
	  * Identifies a unique instance of a Price List
	  */
	public void setM_PriceList_Version_ID (int M_PriceList_Version_ID);

	/** Get Price List Version.
	  * Identifies a unique instance of a Price List
	  */
	public int getM_PriceList_Version_ID();

	public org.compiere.model.I_M_PriceList_Version getM_PriceList_Version() throws RuntimeException;

    /** Column name M_Product_ID */
    public static final String COLUMNNAME_M_Product_ID = "M_Product_ID";

	/** Set Product.
	  * Product, Service, Item
	  */
	public void setM_Product_ID (int M_Product_ID);

	/** Get Product.
	  * Product, Service, Item
	  */
	public int getM_Product_ID();

	public org.compiere.model.I_M_Product getM_Product() throws RuntimeException;

    /** Column name M_Production_ID */
    public static final String COLUMNNAME_M_Production_ID = "M_Production_ID";

	/** Set Production.
	  * Plan for producing a product
	  */
	public void setM_Production_ID (int M_Production_ID);

	/** Get Production.
	  * Plan for producing a product
	  */
	public int getM_Production_ID();

	public org.compiere.model.I_M_Production getM_Production() throws RuntimeException;

    /** Column name PercentageProfitPLimit */
    public static final String COLUMNNAME_PercentageProfitPLimit = "PercentageProfitPLimit";

	/** Set Percentage Profit Price Limit	  */
	public void setPercentageProfitPLimit (BigDecimal PercentageProfitPLimit);

	/** Get Percentage Profit Price Limit	  */
	public BigDecimal getPercentageProfitPLimit();

    /** Column name PercentageProfitPList */
    public static final String COLUMNNAME_PercentageProfitPList = "PercentageProfitPList";

	/** Set Percentage Profit Price List	  */
	public void setPercentageProfitPList (BigDecimal PercentageProfitPList);

	/** Get Percentage Profit Price List	  */
	public BigDecimal getPercentageProfitPList();

    /** Column name PercentageProfitPStd */
    public static final String COLUMNNAME_PercentageProfitPStd = "PercentageProfitPStd";

	/** Set Percentage Profit Price Standart	  */
	public void setPercentageProfitPStd (BigDecimal PercentageProfitPStd);

	/** Get Percentage Profit Price Standart	  */
	public BigDecimal getPercentageProfitPStd();

    /** Column name PriceActual */
    public static final String COLUMNNAME_PriceActual = "PriceActual";

	/** Set Unit Price.
	  * Actual Price 
	  */
	public void setPriceActual (BigDecimal PriceActual);

	/** Get Unit Price.
	  * Actual Price 
	  */
	public BigDecimal getPriceActual();

    /** Column name PriceLastInv */
    public static final String COLUMNNAME_PriceLastInv = "PriceLastInv";

	/** Set Last Invoice Price.
	  * Price of the last invoice for the product
	  */
	public void setPriceLastInv (BigDecimal PriceLastInv);

	/** Get Last Invoice Price.
	  * Price of the last invoice for the product
	  */
	public BigDecimal getPriceLastInv();

    /** Column name PriceLastOrd */
    public static final String COLUMNNAME_PriceLastOrd = "PriceLastOrd";

	/** Set Price Last Order	  */
	public void setPriceLastOrd (BigDecimal PriceLastOrd);

	/** Get Price Last Order	  */
	public BigDecimal getPriceLastOrd();

    /** Column name PriceLastProduction */
    public static final String COLUMNNAME_PriceLastProduction = "PriceLastProduction";

	/** Set PriceLastProduction	  */
	public void setPriceLastProduction (BigDecimal PriceLastProduction);

	/** Get PriceLastProduction	  */
	public BigDecimal getPriceLastProduction();

    /** Column name PriceLimit */
    public static final String COLUMNNAME_PriceLimit = "PriceLimit";

	/** Set Limit Price.
	  * Lowest price for a product
	  */
	public void setPriceLimit (BigDecimal PriceLimit);

	/** Get Limit Price.
	  * Lowest price for a product
	  */
	public BigDecimal getPriceLimit();

    /** Column name PriceLimitEntered */
    public static final String COLUMNNAME_PriceLimitEntered = "PriceLimitEntered";

	/** Set Limit Price.
	  * Entered Limit Price
	  */
	public void setPriceLimitEntered (BigDecimal PriceLimitEntered);

	/** Get Limit Price.
	  * Entered Limit Price
	  */
	public BigDecimal getPriceLimitEntered();

    /** Column name PriceLimitOld */
    public static final String COLUMNNAME_PriceLimitOld = "PriceLimitOld";

	/** Set Limit Price Old.
	  * Old Limit Price
	  */
	public void setPriceLimitOld (BigDecimal PriceLimitOld);

	/** Get Limit Price Old.
	  * Old Limit Price
	  */
	public BigDecimal getPriceLimitOld();

    /** Column name PriceLimitWTax */
    public static final String COLUMNNAME_PriceLimitWTax = "PriceLimitWTax";

	/** Set Price Limit With Tax	  */
	public void setPriceLimitWTax (BigDecimal PriceLimitWTax);

	/** Get Price Limit With Tax	  */
	public BigDecimal getPriceLimitWTax();

    /** Column name PriceList */
    public static final String COLUMNNAME_PriceList = "PriceList";

	/** Set List Price.
	  * List Price
	  */
	public void setPriceList (BigDecimal PriceList);

	/** Get List Price.
	  * List Price
	  */
	public BigDecimal getPriceList();

    /** Column name PriceListEntered */
    public static final String COLUMNNAME_PriceListEntered = "PriceListEntered";

	/** Set List Price Entered.
	  * Entered List Price
	  */
	public void setPriceListEntered (BigDecimal PriceListEntered);

	/** Get List Price Entered.
	  * Entered List Price
	  */
	public BigDecimal getPriceListEntered();

    /** Column name PriceListOld */
    public static final String COLUMNNAME_PriceListOld = "PriceListOld";

	/** Set List Price Old.
	  * Old List Price
	  */
	public void setPriceListOld (BigDecimal PriceListOld);

	/** Get List Price Old.
	  * Old List Price
	  */
	public BigDecimal getPriceListOld();

    /** Column name PriceListWTax */
    public static final String COLUMNNAME_PriceListWTax = "PriceListWTax";

	/** Set PriceList With Tax	  */
	public void setPriceListWTax (BigDecimal PriceListWTax);

	/** Get PriceList With Tax	  */
	public BigDecimal getPriceListWTax();

    /** Column name PriceStd */
    public static final String COLUMNNAME_PriceStd = "PriceStd";

	/** Set Standard Price.
	  * Standard Price
	  */
	public void setPriceStd (BigDecimal PriceStd);

	/** Get Standard Price.
	  * Standard Price
	  */
	public BigDecimal getPriceStd();

    /** Column name PriceStdEntered */
    public static final String COLUMNNAME_PriceStdEntered = "PriceStdEntered";

	/** Set Standart Price.
	  * Entered Standart Price
	  */
	public void setPriceStdEntered (BigDecimal PriceStdEntered);

	/** Get Standart Price.
	  * Entered Standart Price
	  */
	public BigDecimal getPriceStdEntered();

    /** Column name PriceStdOld */
    public static final String COLUMNNAME_PriceStdOld = "PriceStdOld";

	/** Set Standart Price Old.
	  * Old Standart Price
	  */
	public void setPriceStdOld (BigDecimal PriceStdOld);

	/** Get Standart Price Old.
	  * Old Standart Price
	  */
	public BigDecimal getPriceStdOld();

    /** Column name PriceStdWTax */
    public static final String COLUMNNAME_PriceStdWTax = "PriceStdWTax";

	/** Set Price Standard With Tax	  */
	public void setPriceStdWTax (BigDecimal PriceStdWTax);

	/** Get Price Standard With Tax	  */
	public BigDecimal getPriceStdWTax();

    /** Column name Processed */
    public static final String COLUMNNAME_Processed = "Processed";

	/** Set Processed.
	  * The document has been processed
	  */
	public void setProcessed (boolean Processed);

	/** Get Processed.
	  * The document has been processed
	  */
	public boolean isProcessed();

    /** Column name ProcessedOn */
    public static final String COLUMNNAME_ProcessedOn = "ProcessedOn";

	/** Set Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public void setProcessedOn (BigDecimal ProcessedOn);

	/** Get Processed On.
	  * The date+time (expressed in decimal format) when the document has been processed
	  */
	public BigDecimal getProcessedOn();

    /** Column name Processing */
    public static final String COLUMNNAME_Processing = "Processing";

	/** Set Process Now	  */
	public void setProcessing (boolean Processing);

	/** Get Process Now	  */
	public boolean isProcessing();

    /** Column name Product */
    public static final String COLUMNNAME_Product = "Product";

	/** Set Product	  */
	public void setProduct (String Product);

	/** Get Product	  */
	public String getProduct();

    /** Column name Std_Base */
    public static final String COLUMNNAME_Std_Base = "Std_Base";

	/** Set Standard price Base.
	  * Base price for calculating new standard price
	  */
	public void setStd_Base (String Std_Base);

	/** Get Standard price Base.
	  * Base price for calculating new standard price
	  */
	public String getStd_Base();

    /** Column name TaxAmtPriceLimit */
    public static final String COLUMNNAME_TaxAmtPriceLimit = "TaxAmtPriceLimit";

	/** Set Tax Amt Price Limit	  */
	public void setTaxAmtPriceLimit (BigDecimal TaxAmtPriceLimit);

	/** Get Tax Amt Price Limit	  */
	public BigDecimal getTaxAmtPriceLimit();

    /** Column name TaxAmtPriceList */
    public static final String COLUMNNAME_TaxAmtPriceList = "TaxAmtPriceList";

	/** Set Tax Amt Price List	  */
	public void setTaxAmtPriceList (BigDecimal TaxAmtPriceList);

	/** Get Tax Amt Price List	  */
	public BigDecimal getTaxAmtPriceList();

    /** Column name TaxAmtPriceStd */
    public static final String COLUMNNAME_TaxAmtPriceStd = "TaxAmtPriceStd";

	/** Set Tax Amt Price Standard	  */
	public void setTaxAmtPriceStd (BigDecimal TaxAmtPriceStd);

	/** Get Tax Amt Price Standard	  */
	public BigDecimal getTaxAmtPriceStd();

    /** Column name Updated */
    public static final String COLUMNNAME_Updated = "Updated";

	/** Get Updated.
	  * Date this record was updated
	  */
	public Timestamp getUpdated();

    /** Column name UpdatedBy */
    public static final String COLUMNNAME_UpdatedBy = "UpdatedBy";

	/** Get Updated By.
	  * User who updated this records
	  */
	public int getUpdatedBy();

    /** Column name User1_ID */
    public static final String COLUMNNAME_User1_ID = "User1_ID";

	/** Set User Element List 1.
	  * User defined list element #1
	  */
	public void setUser1_ID (int User1_ID);

	/** Get User Element List 1.
	  * User defined list element #1
	  */
	public int getUser1_ID();

	public org.compiere.model.I_C_ElementValue getUser1() throws RuntimeException;

    /** Column name User1W_ID */
    public static final String COLUMNNAME_User1W_ID = "User1W_ID";

	/** Set User Element List 1 W.
	  * User defined list element #1
	  */
	public void setUser1W_ID (int User1W_ID);

	/** Get User Element List 1 W.
	  * User defined list element #1
	  */
	public int getUser1W_ID();

	public org.compiere.model.I_C_ElementValue getUser1W() throws RuntimeException;

    /** Column name User1X_ID */
    public static final String COLUMNNAME_User1X_ID = "User1X_ID";

	/** Set User Element List 1 X.
	  * User defined list element #1
	  */
	public void setUser1X_ID (int User1X_ID);

	/** Get User Element List 1 X.
	  * User defined list element #1
	  */
	public int getUser1X_ID();

	public org.compiere.model.I_C_ElementValue getUser1X() throws RuntimeException;

    /** Column name User1Y_ID */
    public static final String COLUMNNAME_User1Y_ID = "User1Y_ID";

	/** Set User Element List 1 Y.
	  * User defined list element #1
	  */
	public void setUser1Y_ID (int User1Y_ID);

	/** Get User Element List 1 Y.
	  * User defined list element #1
	  */
	public int getUser1Y_ID();

	public org.compiere.model.I_C_ElementValue getUser1Y() throws RuntimeException;

    /** Column name User1Z_ID */
    public static final String COLUMNNAME_User1Z_ID = "User1Z_ID";

	/** Set User Element List 1 Z.
	  * User defined list element #1
	  */
	public void setUser1Z_ID (int User1Z_ID);

	/** Get User Element List 1 Z.
	  * User defined list element #1
	  */
	public int getUser1Z_ID();

	public org.compiere.model.I_C_ElementValue getUser1Z() throws RuntimeException;
}
