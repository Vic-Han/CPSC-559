package client;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class ChecksumUtil {

    public static String generateChecksum(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try(FileInputStream fis = new FileInputStream(file))
        {
            byte[] byteArray = new byte[1024];
            int bytesCount; 

            while((bytesCount = fis.read(byteArray)) != -1 )
            {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        byte[] bytes = digest.digest();

        // Convert to hexadecimal format
        StringBuilder sb = new StringBuilder(); 

        for(byte aByte : bytes)
        {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString(); 

    }

    public static boolean verifyChecksum(File file, String expectedChecksum) throws Exception {
        String calculatedChecksum = generateChecksum(file);
        return calculatedChecksum.equals(expectedChecksum);

    }

}
