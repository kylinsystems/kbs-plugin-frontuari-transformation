/**
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Copyright (C) 2020 Frontuari and contributors (see README.md file).
 */

package net.frontuari.component;

import net.frontuari.base.CustomCalloutFactory;
import net.frontuari.callout.FTUProductionCallout;
import net.frontuari.model.FTUMProduction;

/**
 * Callout Factory
 */
public class CalloutFactory extends CustomCalloutFactory {

	/**
	 * For initialize class. Register the custom callout to build
	 * 
	 * <pre>
	 * protected void initialize() {
	 * 	registerCallout(MTableExample.Table_Name, MTableExample.COLUMNNAME_Text, CPrintPluginInfo.class);
	 * }
	 * </pre>
	 */
	@Override
	protected void initialize() {
		registerCallout(FTUMProduction.Table_Name, FTUMProduction.COLUMNNAME_M_Product_ID, FTUProductionCallout.class);
	}

}
