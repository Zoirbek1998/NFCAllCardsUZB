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

package com.example.nfcallcards.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcF;



import static android.nfc.NfcAdapter.EXTRA_TAG;
import static android.os.Build.VERSION_CODES.GINGERBREAD_MR1;
import static android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH;

import com.example.nfcallcards.nfc.reader.ReaderListener;
import com.example.nfcallcards.nfc.reader.ReaderManager;
public final class CardNfcManager {

	private final Activity activity;
	private final NfcAdapter nfcAdapter;
	private final PendingIntent pendingIntent;

	private static final String[][] TECHLISTS;
	private static final IntentFilter[] TAGFILTERS;
	private int status;

	static {
		TECHLISTS = new String[][]{
				{IsoDep.class.getName()},
				{NfcF.class.getName()}
		};

		IntentFilter techDiscovered = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
		TAGFILTERS = new IntentFilter[]{techDiscovered};
	}

	public CardNfcManager(Activity activity) {
		this.activity = activity;
		this.nfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		this.pendingIntent = PendingIntent.getActivity(
				activity,
				0,
				new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_MUTABLE // Android 12+ uchun
		);
		this.status = getStatus();
	}

	public void onPause() {
		if (nfcAdapter != null) {
			nfcAdapter.disableForegroundDispatch(activity);
		}
	}

	public void onResume() {
		if (nfcAdapter != null) {
			nfcAdapter.enableForegroundDispatch(activity, pendingIntent, TAGFILTERS, TECHLISTS);
		}
	}

	public boolean updateStatus() {
		int newStatus = getStatus();
		if (newStatus != status) {
			status = newStatus;
			return true;
		}
		return false;
	}

	public boolean readCard(Intent intent, ReaderListener listener) {
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		if (tag != null) {
			ReaderManager.readCard(tag, listener);
			return true;
		}
		return false;
	}

	private int getStatus() {
		if (nfcAdapter == null) return -1;
		return nfcAdapter.isEnabled() ? 1 : 0;
	}
}