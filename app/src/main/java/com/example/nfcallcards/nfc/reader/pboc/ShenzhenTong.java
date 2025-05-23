/* NFC Reader is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

NFC Reader is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Wget.  If not, see <http://www.gnu.org/licenses/>.

Additional permission under GNU GPL version 3 section 7 */

package com.example.nfcallcards.nfc.reader.pboc;


import com.example.nfcallcards.nfc.SPEC;

final class ShenzhenTong extends StandardPboc {

    @Override
    protected SPEC.APP getApplicationId() {
        return SPEC.APP.SHENZHENTONG;
    }
}
