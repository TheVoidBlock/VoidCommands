/*
 * Copyright (c) 2022 MisionThi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.thevoidblock.voidcommands;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NbtFormatter {
    public static Text FormatNBTText(Text text) {
        /*
            Get the NBT list that we want to show.
            And we set the symbols up where we look for, so we can detect what to give which color.
         */
        String nbtList = String.valueOf(text.getString());
        Pattern p = Pattern.compile("[{}:\"\\[\\],']", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(nbtList);

        // Create new literalText, which we will be adding to the list.
        MutableText mutableText = Text.empty();

        /*  **Loop through the NBT data**
         *
         */
        // config
        Formatting stringColor = Formatting.GREEN;
        Formatting quotationColor = Formatting.WHITE;
        Formatting separationColor = Formatting.WHITE;
        Formatting integerColor = Formatting.GOLD;
        Formatting typeColor = Formatting.RED;
        Formatting fieldColor = Formatting.AQUA;

        int lastIndex = 0;
        Boolean singleQuotationMark = Boolean.FALSE;
        String lastString = "";


        while (m.find()) {

            /*  **Checking for single quotation marks**
             *  After that we check if the last char was a single quotation mark we can check that by checking the variable "singleQuotationMark".
             *  We do this so the data between the single quotation marks will get the "stringColor".
             *  And we make sure that the single quotation marks get the "quotationColor".
             */
            if (nbtList.charAt(m.start()) == '\'') {
                if (singleQuotationMark.equals(Boolean.FALSE)) { // If false color only the quotation mark
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    singleQuotationMark = Boolean.TRUE;
                }
                else { // Else color the quotation mark and make the rest green
                    mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(stringColor));
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    singleQuotationMark = Boolean.FALSE;
                }
                lastString = String.valueOf(nbtList.charAt(m.start()));
                lastIndex = m.start();
            }


            /*  **Checking if the text is not between the single quotation mark**
             *  1). When the text is not between the single quotation marks the normal formatting will get to work.
             *  2). We check if the char that is found is an opening bracket.
             *  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
             *  4). Now we check for the colon.
             *  5). And lastly we check for the double quotation marks.
             */
            if (singleQuotationMark == Boolean.FALSE) {

                /*  2). We check if the char that is found is an opening bracket.
                        Adds the found char and gives it the "separationColor"
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '{' || nbtList.charAt(m.start()) == '[' ) {
                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(separationColor));
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  3). The closing brackets and the comma (these are also the chars that decide when we go to the next line.).
                        If the char before the found char is a char that indicates what type of variable the text in front of it is.
                            Then: Add the text before the char that indicates the type and give it the "integerColor".
                                  After that add the char type and give it the "typeColor".
                            Else: Give all the text in front of the count char the "integerColor".
                        Now we add the found char with the "separationColor" (includes comma's)
                        If the char was a comma add a space (this way it's more readable)
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '}' || nbtList.charAt(m.start()) == ']' || nbtList.charAt(m.start()) == ',') {
                    if (nbtList.charAt(m.start()-1) == 's' || nbtList.charAt(m.start()-1) == 'S' ||
                            nbtList.charAt(m.start()-1) == 'b' || nbtList.charAt(m.start()-1) == 'B' ||
                            nbtList.charAt(m.start()-1) == 'l' || nbtList.charAt(m.start()-1) == 'L' ||
                            nbtList.charAt(m.start()-1) == 'f' || nbtList.charAt(m.start()-1) == 'F'
                    ) {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start()-1)).formatted(integerColor));
                        mutableText.append(Text.literal(nbtList.substring(m.start()-1,m.start())).formatted(typeColor));

                    }
                    else {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(integerColor));
                    }

                    mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColor);

                    if (nbtList.charAt(m.start()) == ',') { mutableText.append(Text.literal(" ").formatted(separationColor)); }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();
                }

                /*  4). Now we check for the colon. (:)
                        If the last string doesn't equal the double quotation mark. (when it is between it should only get the "stringColor")
                            Then: Add the text in front of the colon and give it the "fieldColor".
                                  Add the found char and give it the "separationColor".
                                  Add a space so it's easier to read.
                                  Stores the lastString and lastIndex

                 */
                if (nbtList.charAt(m.start()) == ':') { // 4).
                    if (!lastString.equals("\"")) {
                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(fieldColor));

                        mutableText.append((Text.literal(String.valueOf(nbtList.charAt(m.start())))).formatted(separationColor));
                        mutableText.append(Text.literal(" ").formatted(separationColor));
                        lastString = String.valueOf(nbtList.charAt(m.start()));
                        lastIndex = m.start();
                    }

                }
                /*  5). And lastly we check for the double quotation marks.
                        If the last char was a " too.
                            Then: If the string between the columns is longer then the linesStep
                                Then: Convert the string between the quotation marks to .... "lstringColor"
                                      And add the removed chars to the "removeCharters" variable.
                                Else: Add the string and give it the "stringColor"
                            Else: Only add the " since it's the first one.
                        Stores the lastString and lastIndex
                 */
                if (nbtList.charAt(m.start()) == '"') {
                    if (lastString.equals("\"")){

                        mutableText.append(Text.literal(nbtList.substring(lastIndex+1,m.start())).formatted(stringColor));

                        mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    }
                    else {
                        mutableText.append(Text.literal(String.valueOf(nbtList.charAt(m.start()))).formatted(quotationColor));
                    }
                    lastString = String.valueOf(nbtList.charAt(m.start()));
                    lastIndex = m.start();

                }
            }
        }

        return mutableText;
    }

}