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

/** Generated Interface for BSCA_PC_MultiOrgs
 *  @author iDempiere (generated) 
 *  @version Release 3.1
 */
@SuppressWarnings("all")
public interface I_BSCA_PC_MultiOrgs 
{

    /** TableName=BSCA_PC_MultiOrgs */
    public static final String Table_Name = "BSCA_PC_MultiOrgs";

    /** AD_Table_ID=1000541 */
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

    /** Column name BSCA_PCGenerated_ID */
    public static final String COLUMNNAME_BSCA_PCGenerated_ID = "BSCA_PCGenerated_ID";

	/** Set Price Change Generated	  */
	public void setBSCA_PCGenerated_ID (int BSCA_PCGenerated_ID);

	/** Get Price Change Generated	  */
	public int getBSCA_PCGenerated_ID();

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PCGenerated() throws RuntimeException;

    /** Column name BSCA_PC_MultiOrgs_ID */
    public static final String COLUMNNAME_BSCA_PC_MultiOrgs_ID = "BSCA_PC_MultiOrgs_ID";

	/** Set Price Change Multi Organizations	  */
	public void setBSCA_PC_MultiOrgs_ID (int BSCA_PC_MultiOrgs_ID);

	/** Get Price Change Multi Organizations	  */
	public int getBSCA_PC_MultiOrgs_ID();

    /** Column name BSCA_PC_MultiOrgs_UU */
    public static final String COLUMNNAME_BSCA_PC_MultiOrgs_UU = "BSCA_PC_MultiOrgs_UU";

	/** Set BSCA_PC_MultiOrgs_UU	  */
	public void setBSCA_PC_MultiOrgs_UU (String BSCA_PC_MultiOrgs_UU);

	/** Get BSCA_PC_MultiOrgs_UU	  */
	public String getBSCA_PC_MultiOrgs_UU();

    /** Column name BSCA_PriceChange_ID */
    public static final String COLUMNNAME_BSCA_PriceChange_ID = "BSCA_PriceChange_ID";

	/** Set Price Change	  */
	public void setBSCA_PriceChange_ID (int BSCA_PriceChange_ID);

	/** Get Price Change	  */
	public int getBSCA_PriceChange_ID();

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PriceChange() throws RuntimeException;

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

    /** Column name IsSamePurchasePrice */
    public static final String COLUMNNAME_IsSamePurchasePrice = "IsSamePurchasePrice";

	/** Set Same Purchase Price	  */
	public void setIsSamePurchasePrice (boolean IsSamePurchasePrice);

	/** Get Same Purchase Price	  */
	public boolean isSamePurchasePrice();

    /** Column name IsSameSalePrice */
    public static final String COLUMNNAME_IsSameSalePrice = "IsSameSalePrice";

	/** Set Same Sale Price	  */
	public void setIsSameSalePrice (boolean IsSameSalePrice);

	/** Get Same Sale Price	  */
	public boolean isSameSalePrice();

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

    /** Column name PurchasePriceEntered */
    public static final String COLUMNNAME_PurchasePriceEntered = "PurchasePriceEntered";

	/** Set Purchase Price Entered	  */
	public void setPurchasePriceEntered (BigDecimal PurchasePriceEntered);

	/** Get Purchase Price Entered	  */
	public BigDecimal getPurchasePriceEntered();

    /** Column name SalePriceEntered */
    public static final String COLUMNNAME_SalePriceEntered = "SalePriceEntered";

	/** Set Sale Price Entered	  */
	public void setSalePriceEntered (BigDecimal SalePriceEntered);

	/** Get Sale Price Entered	  */
	public BigDecimal getSalePriceEntered();

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
}
