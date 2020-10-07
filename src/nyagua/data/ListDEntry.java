/*
 * Nyagua - Aquarium Manager
 *    Copyright (C) 2014 Rudi Giacomini Pilon
 *
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
 */

package nyagua.data;

/**
 *This class manage two generic fiels 
 * to load them in jList and the 
 * retrieve the related values
 *
 * @author Rudi Giacomini Pilon
 */
public class ListDEntry {
    private final int value;
    private final String label;

    public ListDEntry(int id, String label){
        this.value=id;
        this.label=label;
    }

    public int getValue(){
        return value;
    }

    public String getLabel(){
        return label;
    }

    @Override
    public String toString(){
        return value + " \t" + label;//NOI18N
    }
}
