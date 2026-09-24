package com.hail.app.crypto
import android.util.Base64
import java.security.KeyFactory
import java.security.spec.MGF1ParameterSpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

interface CryptoManager { fun encrypt(plaintext: String, publicKeyPem: String): String }

/** RSA-OAEP, SHA-256, MGF1-SHA-256, UTF-8 in, Base64 out. */
class RsaOaepCryptoManager : CryptoManager {
    override fun encrypt(plaintext: String, publicKeyPem: String): String {
        val der = Base64.decode(publicKeyPem.replace(Regex("-----[A-Z ]+-----|\\s"), ""), Base64.DEFAULT)
        val key = KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(der))
        val spec = OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT)
        val c = Cipher.getInstance("RSA/ECB/OAEPPadding").apply { init(Cipher.ENCRYPT_MODE, key, spec) }
        return Base64.encodeToString(c.doFinal(plaintext.toByteArray(Charsets.UTF_8)), Base64.NO_WRAP)
    }
}
