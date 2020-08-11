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

import java.sql.ResultSet;
import java.util.Properties;
import org.compiere.model.*;

/** Generated Model for BSCA_ProductValue
 *  @author iDempiere (generated) 
 *  @version Release 3.1 - $Id$ */
public class X_BSCA_ProductValue extends PO implements I_BSCA_ProductValue, I_Persistent 
{

	/**
	 *
	 */
	private static final long serialVersionUID = 20171019L;

    /** Standard Constructor */
    public X_BSCA_ProductValue (Properties ctx, int BSCA_ProductValue_ID, String trxName)
    {
      super (ctx, BSCA_ProductValue_ID, trxName);
      /** if (BSCA_ProductValue_ID == 0)
        {
			setBSCA_ProductValue_ID (0);
			setIsMasterValue (false);
// N
			setM_Product_ID (0);
			setPriceType (null);
        } */
    }

    /** Load Constructor */
    public X_BSCA_ProductValue (Properties ctx, ResultSet rs, String trxName)
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
      StringBuffer sb = new StringBuffer ("X_BSCA_ProductValue[")
        .append(get_ID()).append("]");
      return sb.toString();
    }

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

	/** Set BSCA_ProductValue_UU.
		@param BSCA_ProductValue_UU BSCA_ProductValue_UU	  */
	public void setBSCA_ProductValue_UU (String BSCA_ProductValue_UU)
	{
		set_Value (COLUMNNAME_BSCA_ProductValue_UU, BSCA_ProductValue_UU);
	}

	/** Get BSCA_ProductValue_UU.
		@return BSCA_ProductValue_UU	  */
	public String getBSCA_ProductValue_UU () 
	{
		return (String)get_Value(COLUMNNAME_BSCA_ProductValue_UU);
	}

	/** Set IsMasterValue.
		@param IsMasterValue IsMasterValue	  */
	public void setIsMasterValue (boolean IsMasterValue)
	{
		set_Value (COLUMNNAME_IsMasterValue, Boolean.valueOf(IsMasterValue));
	}

	/** Get IsMasterValue.
		@return IsMasterValue	  */
	public boolean isMasterValue () 
	{
		Object oo = get_Value(COLUMNNAME_IsMasterValue);
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

	/** Price List = L */
	public static final String PRICETYPE_PriceList = "L";
	/** Standard Price = S */
	public static final String PRICETYPE_StandardPrice = "S";
	/** Limit Price = X */
	public static final String PRICETYPE_LimitPrice = "X";
	/** Set PriceType.
		@param PriceType PriceType	  */
	public void setPriceType (String PriceType)
	{

		set_Value (COLUMNNAME_PriceType, PriceType);
	}

	/** Get PriceType.
		@return PriceType	  */
	public String getPriceType () 
	{
		return (String)get_Value(COLUMNNAME_PriceType);
	}

	/** Set Search Key.
		@param Value 
		Search key for the record in the format required - must be unique
	  */
	public void setValue (String Value)
	{
		set_Value (COLUMNNAME_Value, Value);
	}

	/** Get Search Key.
		@return Search key for the record in the format required - must be unique
	  */
	public String getValue () 
	{
		return (String)get_Value(COLUMNNAME_Value);
	}
}