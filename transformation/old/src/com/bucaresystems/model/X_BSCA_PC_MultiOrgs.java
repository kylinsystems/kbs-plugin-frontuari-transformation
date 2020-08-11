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
import java.util.Properties;
import org.compiere.model.*;
import org.compiere.util.Env;

/** Generated Model for BSCA_PC_MultiOrgs
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_BSCA_PC_MultiOrgs extends PO implements I_BSCA_PC_MultiOrgs, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20190117L;

    /** Standard Constructor */
    public X_BSCA_PC_MultiOrgs (Properties ctx, int BSCA_PC_MultiOrgs_ID, String trxName)
    {
      super (ctx, BSCA_PC_MultiOrgs_ID, trxName);
      /** if (BSCA_PC_MultiOrgs_ID == 0)
        {
			setBSCA_PC_MultiOrgs_ID (0);
			setBSCA_PriceChange_ID (0);
			setIsSamePurchasePrice (false);
// N
			setIsSameSalePrice (false);
// N
			setM_Product_ID (0);
// @M_Product_ID@
        } */
    }

    /** Load Constructor */
    public X_BSCA_PC_MultiOrgs (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BSCA_PC_MultiOrgs[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PCGenerated() throws RuntimeException
    {
		return (com.bucaresystems.model.I_BSCA_PriceChange)MTable.get(getCtx(), com.bucaresystems.model.I_BSCA_PriceChange.Table_Name)
			.getPO(getBSCA_PCGenerated_ID(), get_TrxName());	}

	/** Set Price Change Generated.
		@param BSCA_PCGenerated_ID Price Change Generated	  */
	public void setBSCA_PCGenerated_ID (int BSCA_PCGenerated_ID)
	{
		if (BSCA_PCGenerated_ID < 1) 
			set_Value (COLUMNNAME_BSCA_PCGenerated_ID, null);
		else 
			set_Value (COLUMNNAME_BSCA_PCGenerated_ID, Integer.valueOf(BSCA_PCGenerated_ID));
	}

	/** Get Price Change Generated.
		@return Price Change Generated	  */
	public int getBSCA_PCGenerated_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_PCGenerated_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set Price Change Multi Organizations.
		@param BSCA_PC_MultiOrgs_ID Price Change Multi Organizations	  */
	public void setBSCA_PC_MultiOrgs_ID (int BSCA_PC_MultiOrgs_ID)
	{
		if (BSCA_PC_MultiOrgs_ID < 1) 
			set_ValueNoCheck (COLUMNNAME_BSCA_PC_MultiOrgs_ID, null);
		else 
			set_ValueNoCheck (COLUMNNAME_BSCA_PC_MultiOrgs_ID, Integer.valueOf(BSCA_PC_MultiOrgs_ID));
	}

	/** Get Price Change Multi Organizations.
		@return Price Change Multi Organizations	  */
	public int getBSCA_PC_MultiOrgs_ID () 
	{
		Integer ii = (Integer)get_Value(COLUMNNAME_BSCA_PC_MultiOrgs_ID);
		if (ii == null)
			 return 0;
		return ii.intValue();
	}

	/** Set BSCA_PC_MultiOrgs_UU.
		@param BSCA_PC_MultiOrgs_UU BSCA_PC_MultiOrgs_UU	  */
	public void setBSCA_PC_MultiOrgs_UU (String BSCA_PC_MultiOrgs_UU)
	{
		set_ValueNoCheck (COLUMNNAME_BSCA_PC_MultiOrgs_UU, BSCA_PC_MultiOrgs_UU);
	}

	/** Get BSCA_PC_MultiOrgs_UU.
		@return BSCA_PC_MultiOrgs_UU	  */
	public String getBSCA_PC_MultiOrgs_UU () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_PC_MultiOrgs_UU);
	}

	public com.bucaresystems.model.I_BSCA_PriceChange getBSCA_PriceChange() throws RuntimeException
    {
		return (com.bucaresystems.model.I_BSCA_PriceChange)MTable.get(getCtx(), com.bucaresystems.model.I_BSCA_PriceChange.Table_Name)
			.getPO(getBSCA_PriceChange_ID(), get_TrxName());	}

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

	/** Set Same Purchase Price.
		@param IsSamePurchasePrice Same Purchase Price	  */
	public void setIsSamePurchasePrice (boolean IsSamePurchasePrice)
	{
		set_Value (COLUMNNAME_IsSamePurchasePrice, Boolean.valueOf(IsSamePurchasePrice));
	}

	/** Get Same Purchase Price.
		@return Same Purchase Price	  */
	public boolean isSamePurchasePrice () 
	{
		Object oo = get_Value(COLUMNNAME_IsSamePurchasePrice);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
	}

	/** Set Same Sale Price.
		@param IsSameSalePrice Same Sale Price	  */
	public void setIsSameSalePrice (boolean IsSameSalePrice)
	{
		set_Value (COLUMNNAME_IsSameSalePrice, Boolean.valueOf(IsSameSalePrice));
	}

	/** Get Same Sale Price.
		@return Same Sale Price	  */
	public boolean isSameSalePrice () 
	{
		Object oo = get_Value(COLUMNNAME_IsSameSalePrice);
		if (oo != null) 
		{
			 if (oo instanceof Boolean) 
				 return ((Boolean)oo).booleanValue(); 
			return "Y".equals(oo);
		}
		return false;
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
			set_Value (COLUMNNAME_M_Product_ID, null);
		else 
			set_Value (COLUMNNAME_M_Product_ID, Integer.valueOf(M_Product_ID));
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

	/** Set Purchase Price Entered.
		@param PurchasePriceEntered Purchase Price Entered	  */
	public void setPurchasePriceEntered (BigDecimal PurchasePriceEntered)
	{
		set_Value (COLUMNNAME_PurchasePriceEntered, PurchasePriceEntered);
	}

	/** Get Purchase Price Entered.
		@return Purchase Price Entered	  */
	public BigDecimal getPurchasePriceEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_PurchasePriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}

	/** Set Sale Price Entered.
		@param SalePriceEntered Sale Price Entered	  */
	public void setSalePriceEntered (BigDecimal SalePriceEntered)
	{
		set_Value (COLUMNNAME_SalePriceEntered, SalePriceEntered);
	}

	/** Get Sale Price Entered.
		@return Sale Price Entered	  */
	public BigDecimal getSalePriceEntered () 
	{
		BigDecimal bd = (BigDecimal)get_Value(COLUMNNAME_SalePriceEntered);
		if (bd == null)
			 return Env.ZERO;
		return bd;
	}
}