package cs370_term_project;

public class SimpleEncryption {
    private static final String DEFAULT_KEY = "ChatSecretKey123";
    private String key;
    
    public SimpleEncryption() {
        this.key = DEFAULT_KEY;
    }
    
    public SimpleEncryption(String key) {
        this.key = key;
    }
    
    public String encrypt(String message) {
        if (message == null) return null;
        
        char[] result = new char[message.length()];
        
        for (int i = 0; i < message.length(); i++) {
            result[i] = (char)(message.charAt(i) ^ key.charAt(i % key.length()));
        }
        
        return new String(result);
    }
    
    public String decrypt(String encryptedMessage) {
        return encrypt(encryptedMessage);
    }
}
