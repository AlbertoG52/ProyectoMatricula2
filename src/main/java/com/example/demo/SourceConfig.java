package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;

@Configuration
public class SourceConfig {

    @Bean
    public DataSource dataSource() {
        try {
            // Extraer el wallet a una carpeta temporal
            String walletPath = extractWalletToTempDir();

            // Utilizar esa ruta en la URL de conexión
            return DataSourceBuilder.create()
                    .url("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=adb.us-ashburn-1.oraclecloud.com)(PORT=1522))(CONNECT_DATA=(SERVICE_NAME=AEZF815LXKTRNNK_SIUADB1_high.adb.oraclecloud.com))(Security=(MY_WALLET_DIRECTORY=" + walletPath + ")))")
                    .username("aguerrero")
                    .password("MiNueva.ClaveSegura25")
                    .driverClassName("oracle.jdbc.OracleDriver")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al configurar la conexión a la base de datos", e);
        }
    }

    // Función para extraer el wallet a una carpeta temporal
    private String extractWalletToTempDir() throws Exception {
        // Crear una carpeta temporal
        File tempDir = Files.createTempDirectory("wallet").toFile();

        // Obtener el recurso (la carpeta Wallet_siuadb1 dentro del JAR)
        String[] files = {"cwallet.sso", "tnsnames.ora", "sqlnet.ora", "ewallet.p12", "keystore.jks"};
        
        for (String fileName : files) {
            try (InputStream walletStream = getClass().getClassLoader().getResourceAsStream("Wallet_siuadb1/" + fileName)) {
                if (walletStream == null) {
                    throw new RuntimeException("No se pudo encontrar el archivo del wallet: " + fileName);
                }
                File outputFile = new File(tempDir, fileName);
                try (FileOutputStream outStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = walletStream.read(buffer)) != -1) {
                        outStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }

        // Retorna la ruta absoluta de la carpeta temporal donde se extrajo el wallet
        return tempDir.getAbsolutePath();
    }
}


