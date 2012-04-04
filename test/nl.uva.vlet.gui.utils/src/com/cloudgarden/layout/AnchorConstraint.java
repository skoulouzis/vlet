/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: AnchorConstraint.java,v 1.3 2011-04-18 12:27:15 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:15 $
 */ 
// source: 

package com.cloudgarden.layout;

/**
 * Used by both AnchorLayout (SWT) and AnchorLayoutManager (Swing)
 */
public class AnchorConstraint {

	/**
	 * Meaning: This side is not anchored.
	 */
	public static final int ANCHOR_NONE = 0;

	/**
	 * (Relative anchor) Meaning: This side is anchored so that it always 
	 * occurs a fixed fraction of
	 * the distance along it's parent's side. The position is calculated by
	 * the formula " position = (parent side)*(value)/1000 " so for
	 * instance if top=100 and topType == ANCHOR_REL then the
	 * value of y for this side would be (parent height)*top/1000.
	 */
	public static final int ANCHOR_REL = 1;

	/**
	 * (Absolute anchor) Meaning: This side is anchored a fixed distance
	 * in pixels (given by the value for this side) from it's parent's respective side.
	 * For instance, if bottomType == ANCHOR_ABS and bottom = 100 then the
	 * bottom side of this component will remain fixed 100 pixels from
	 * the bottom side of it's parent container.
	 */
	public static final int ANCHOR_ABS = 2;

	public int top;
	public int bottom;
	public int left;
	public int right;
	public int topType;
	public int bottomType;
	public int rightType;
	public int leftType;

	public AnchorConstraint() {
		this(0, 0, 0, 0, ANCHOR_NONE, ANCHOR_NONE, ANCHOR_NONE, ANCHOR_NONE);
	}

	/**
	 * Creates an AnchorConstraint.
	 * @param top - value (relative or absolute) for top side
	 * @param right - like 'top' but for right side
	 * @param bottom - like 'top' but for bottom side
	 * @param left - like 'top' but for left side
	 * @param topType - either ANCHOR_ABS, ANCHOR_REL or ANCHOR_NONE
	 * to indicate whether the 'top' parameter is an absolute value (in pixels) or
	 * a fractional value (in 1/1000 ths) of the height of this component's parent,
	 * denoting where the anchor will be applied (if at all).
	 * @param rightType - like 'topType' but for right side
	 * @param bottomType - like 'topType' but for bottom side
	 * @param leftType - like 'topType' but for left side
	 */
	public AnchorConstraint(
		int top,
		int right,
		int bottom,
		int left,
		int topType,
		int rightType,
		int bottomType,
		int leftType) {
		this.top = top;
		this.bottom = bottom;
		this.left = left;
		this.right = right;
		this.topType = topType;
		this.rightType = rightType;
		this.bottomType = bottomType;
		this.leftType = leftType;
	}

}
