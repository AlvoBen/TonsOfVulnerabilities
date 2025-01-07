package com.sap.engine.lib.xml.signature.encryption;

import java.security.Key;
import java.security.PublicKey;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.w3c.dom.Node;

import com.sap.engine.lib.xml.signature.Constants;
import com.sap.engine.lib.xml.signature.SignatureException;
import com.sap.engine.lib.xml.signature.crypto.Reusable;
//import com.sap.engine.lib.xml.signature.crypto.impl.ReusableZipAESCipher;
import com.sap.engine.lib.xml.signature.elements.GenericElement;
import com.sap.engine.lib.xml.signature.elements.KeyInfo;
import com.sap.engine.lib.xml.signature.encryption.keytrans.KeyTransporter;
import com.sap.engine.lib.xml.signature.transform.Transformation;
import com.sap.engine.lib.xml.util.BASE64Decoder;
import com.sap.engine.lib.xml.util.BASE64Encoder;

public class EncryptedKeyGenerator extends XMLCryptor {

	private EncryptedKey ek = null;
	private GenericElement refList = null;

	private boolean showKeyInfo = false;

	public EncryptedKeyGenerator() {
	}

	public void init(Node parent) throws SignatureException {
		if (parent != null) {
			ek = new EncryptedKey(parent);
		} else {
			ek = new EncryptedKey();
		}
	}

	public void setRecipient(String recipient) throws SignatureException {
		ek.setRecipient(recipient);
	}

	public void setCarriedKeyName(String carriedKeyName) throws SignatureException {
		ek.setCarriedKeyName(carriedKeyName);
	}

	public void startNewReferenceList() throws SignatureException {
		refList = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "ReferenceList", ek);
	}

	public void setType(String type) {
		ek.setType(type);
	}
	public void addReference(Transformation[] transforms, String uri, boolean dataReference) throws SignatureException {
		if (transforms != null) {
			EncReference cr = new EncReference(uri, dataReference);
			cr.setTransforms(transforms);
			cr.construct(refList);
		}
	}

	public EncryptedKey getEncryptedKey(String $algorithmURI, Key wrappingKey, Key wrappedKey) throws SignatureException {
		this.key = wrappingKey;
		String encrypted = encryptKey($algorithmURI, wrappedKey);
		GenericElement first = ek.getFirstChild();
		GenericElement encMeth = new GenericElement(Constants.ENCRYPTION_SPEC_NS, Constants.STANDARD_ENC_PREFIX + "EncryptionMethod", ek);
		encMeth.setAttribute("Algorithm", $algorithmURI);
		if (first != null && first.getDomRepresentation() != encMeth.getDomRepresentation()) {
			ek.insertBefore(encMeth, first);
		}

		CipherData cd = new CipherData(ek);
		cd.setCipherValue(encrypted);
		cd.construct();

		if (showKeyInfo) {
			KeyInfo kInfo = new KeyInfo(Constants.SIGNATURE_SPEC_NS, Constants.STANDARD_PREFIX + "KeyInfo", ek);

			kInfo.setPublicKey((PublicKey) key);

			if (key != null) {
				kInfo.addKeyValue(key.getAlgorithm());
			}

		}

		ek.construct();

		return ek;
	}

	public Key extractKey(EncryptedKey k, Key wrapping, String secretAlgorithm) throws SignatureException {
		this.key = wrapping;
		Reusable reusable = null;
		try {
			k.initializeDescendants();
			String cipherdata = k.getCipherData().getFirstChild().getNodeValue();
			algorithmURI = k.getDirectChild(Constants.ENCRYPTION_SPEC_NS, "EncryptionMethod").getAttribute("Algorithm", null, null);
            // compatibility with NW04 and NW04s
            if ("AES".equals(secretAlgorithm)){
              secretAlgorithm = Constants.ALG_ENC_AES128;
            } else if ("DESede".equals(secretAlgorithm)){
              secretAlgorithm = Constants.ALG_ENC_TRIPLEDES;
            }
			reusable = Reusable.getInstance(secretAlgorithm.concat("_gen"));
			byte[] decoded = decryptKey(algorithmURI, cipherdata.getBytes()); //$JL-I18N$
			String keyAlg = null;
			if (Constants.ALG_ENC_AES128.equals(secretAlgorithm)) {
				keyAlg = "AES";
			} else if (Constants.ALG_ENC_AES192.equals(secretAlgorithm)) {
				keyAlg = "AES";
			} else if (Constants.ALG_ENC_AES256.equals(secretAlgorithm)) {
				keyAlg = "AES";
			} else 	if (Constants.ALG_ENC_TRIPLEDES.equals(secretAlgorithm)) {
				keyAlg = "DESede";
			}
//      if (ReusableZipAESCipher.ZIP_AES_128_URI.equals(secretAlgorithm)) {
//        keyAlg = "AES";
//      }
			SecretKeySpec skc = new SecretKeySpec(decoded, keyAlg);
			SecretKeyFactory f = (SecretKeyFactory) reusable.getInternal();
			return f.generateSecret(skc);
		} catch (SignatureException e) {
			throw e;
		} catch (Exception e) {
			throw new SignatureException("Error extracting key", new Object[] { k, wrapping, secretAlgorithm }, e);
		} finally {
			if (reusable != null) {
				reusable.release();
			}
		}
	}

	public boolean getShowKeyInfo() {
		return showKeyInfo;
	}

	public void setShowKeyInfo(boolean showKeyInfo) {
		this.showKeyInfo = showKeyInfo;
	}

	public String encryptKey(String $algorithmURI, Key wrappedKey) throws SignatureException {
		KeyTransporter tr = KeyTransporter.getInstance($algorithmURI);
		return new String(BASE64Encoder.encode(tr.encrypt(key, wrappedKey))); //$JL-I18N$
	}

	public byte[] decryptKey(String $algorithmURI, byte[] wrappedKey) throws SignatureException {
		KeyTransporter tr = KeyTransporter.getInstance($algorithmURI);
		return tr.decrypt(key, BASE64Decoder.decode(wrappedKey));
	}

}
