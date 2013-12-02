/***************************************************************************
 * Project file:    NPlugins - NCore - InventorySorter.java                *
 * Full Class name: fr.ribesg.bukkit.ncore.utils.InventorySorter           *
 *                                                                         *
 *                Copyright (c) 2013 Ribesg - www.ribesg.fr                *
 *   This file is under GPLv3 -> http://www.gnu.org/licenses/gpl-3.0.txt   *
 *    Please contact me at ribesg[at]yahoo.fr if you improve this file!    *
 ***************************************************************************/

package fr.ribesg.bukkit.ncore.utils;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Comparator;

public class InventorySorter {

	private static Comparator<ItemStack> comparator;

	private static Comparator<ItemStack> getComparator() {
		if (comparator == null) {
			comparator = new Comparator<ItemStack>() {

				@Override
				public int compare(ItemStack o1, ItemStack o2) {
					if (o2 == null) {
						return -1;
					} else if (o1 == null) {
						return 1;
					} else {
						final int compareIds = Integer.compare(o1.getTypeId(), o2.getTypeId());
						if (compareIds != 0) {
							return compareIds;
						} else {
							return Short.compare(o1.getDurability(), o2.getDurability());
						}
					}
				}
			};
		}
		return comparator;
	}

	public static void sort(final Inventory inventory) {
		final ItemStack[] content = inventory.getContents();
		Arrays.sort(content, getComparator());
		inventory.setContents(content);
	}
}