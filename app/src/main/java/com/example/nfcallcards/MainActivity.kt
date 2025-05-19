@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.nfcallcards
import android.content.Intent
import android.nfc.NfcAdapter
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nfcallcards.creditCardNfcReader.CardNfcAsyncTask
import com.example.nfcallcards.creditCardNfcReader.utils.CardNfcUtils
import com.example.nfcallcards.nfc.CardNfcManager
import com.example.nfcallcards.nfc.SPEC
import com.example.nfcallcards.nfc.bean.Card
import com.example.nfcallcards.ui.theme.NFCAllCardsTheme


class MainActivity : ComponentActivity(), CardNfcAsyncTask.CardNfcInterface {

    private lateinit var mCardNfcManager: CardNfcManager
    private var mCardNfcAsyncTask: CardNfcAsyncTask? = null
    private var mNfcAdapter: NfcAdapter? = null
    private var mCardNfcUtils: CardNfcUtils? = null
    private var mIntentFromCreate = false

    private var card: String? by mutableStateOf(null)
    private var cardType: String? by mutableStateOf(null)
    private var expiredDate: String? by mutableStateOf(null)
    private var infoText by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)
        mCardNfcUtils = CardNfcUtils(this)
        mIntentFromCreate = true
        mCardNfcManager = CardNfcManager(this)

        setContent {
            NFCAllCardsTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(title = { Text("NFC Reader") })
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (infoText.isNotBlank()) infoText else "Kartani tuting...",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        if (!mCardNfcManager.readCard(intent) { event, objs ->
                if (event == SPEC.EVENT.FINISHED) {
                    val card: Card? = objs.firstOrNull() as? Card
                    buildResult(card)
                }
            }) {
            Toast.makeText(this, "Xatolik!", Toast.LENGTH_SHORT).show()
        }

        if (mNfcAdapter != null && mNfcAdapter!!.isEnabled) {
            mCardNfcAsyncTask = CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build()
        }
    }

    private fun buildResult(card: Card?) {
        if (card != null && !card.hasReadingException()) {
            val number = card.serialAndDate.cardNumber
            val expDate = card.serialAndDate.cardExpiredDate
            infoText = "$number\n$expDate"
            Toast.makeText(this, infoText, Toast.LENGTH_SHORT).show()
        }
    }

    override fun startNfcReadCard() {}

    override fun cardIsReadyToRead() {
        card = mCardNfcAsyncTask?.cardNumber?.let { getPrettyCardNumber(it) }
        expiredDate = mCardNfcAsyncTask?.cardExpireDate
        cardType = mCardNfcAsyncTask?.cardType

        val cardCvv: Int = mCardNfcAsyncTask?.cardCvv ?: 0
        infoText = "$card\n$expiredDate\n$cardType\nCVV: $cardCvv"
        parseCardType(cardType ?: "")
    }

    override fun doNotMoveCardSoFast() {}
    override fun unknownEmvCard() {}
    override fun cardWithLockedNfc() {}

    override fun finishNfcReadCard() {
        mCardNfcAsyncTask = null
    }

    override fun onPause() {
        super.onPause()
        mCardNfcManager.onPause()
    }

    override fun onResume() {
        super.onResume()
        mCardNfcManager.onResume()
    }

    private fun getPrettyCardNumber(card: String): String {
        val div = " - "
        return "${card.substring(0, 4)}$div${card.substring(4, 8)}$div${card.substring(8, 12)}$div${card.substring(12, 16)}"
    }

    private fun parseCardType(cardType: String) {
        when (cardType) {
            CardNfcAsyncTask.CARD_UNKNOWN -> {
                Toast.makeText(this, "unknown", Toast.LENGTH_LONG).show()
            }

            CardNfcAsyncTask.CARD_VISA -> {
                Log.d("CARD_TYPE", "Visa")
            }

            CardNfcAsyncTask.CARD_MASTER_CARD -> {
                Log.d("CARD_TYPE", "MasterCard")
            }

            CardNfcAsyncTask.CARD_HUMO -> {
                Toast.makeText(this, "HUMO READING", Toast.LENGTH_SHORT).show()
            }
        }
    }
}