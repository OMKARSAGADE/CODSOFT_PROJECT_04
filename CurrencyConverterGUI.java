import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyConverterGUI extends JFrame {

    private static final String API_KEY = "4b5392d0f728687c5f58be8d"; // Your API key
    private static final String API_URL = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/";

    private JTextField baseCurrencyField;
    private JTextField targetCurrencyField;
    private JTextField amountField;
    private JLabel resultLabel;

    public CurrencyConverterGUI() {
        setTitle("Currency Converter");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        // Create components
        JLabel baseCurrencyLabel = new JLabel("Base Currency (e.g., USD):");
        baseCurrencyField = new JTextField();
        
        JLabel targetCurrencyLabel = new JLabel("Target Currency (e.g., EUR):");
        targetCurrencyField = new JTextField();
        
        JLabel amountLabel = new JLabel("Amount to Convert:");
        amountField = new JTextField();
        
        JButton convertButton = new JButton("Convert");
        resultLabel = new JLabel("Converted Amount: ");
        
        // Add action listener to the button
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        // Add components to the frame
        add(baseCurrencyLabel);
        add(baseCurrencyField);
        add(targetCurrencyLabel);
        add(targetCurrencyField);
        add(amountLabel);
        add(amountField);
        add(convertButton);
        add(resultLabel);

        // Set background color
        getContentPane().setBackground(Color.CYAN);
        setVisible(true);
    }

    private void convertCurrency() {
        String baseCurrency = baseCurrencyField.getText().toUpperCase();
        String targetCurrency = targetCurrencyField.getText().toUpperCase();
        double amount;

        try {
            amount = Double.parseDouble(amountField.getText());
            double exchangeRate = fetchExchangeRate(baseCurrency, targetCurrency);
            double convertedAmount = amount * exchangeRate;
            resultLabel.setText(String.format("Converted Amount: %.2f %s", convertedAmount, targetCurrency));
        } catch (NumberFormatException e) {
            resultLabel.setText("Error: Invalid amount.");
        } catch (Exception e) {
            resultLabel.setText("Error: " + e.getMessage());
        }
    }

    private double fetchExchangeRate(String baseCurrency, String targetCurrency) throws Exception {
        String urlString = API_URL + baseCurrency;
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        StringBuilder response = new StringBuilder();
        String output;
        while ((output = br.readLine()) != null) {
            response.append(output);
        }
        conn.disconnect();

        // Parse the JSON response to get the exchange rate
        String jsonResponse = response.toString();
        String rateString = jsonResponse.split(targetCurrency + "\":")[1].split(",")[0];
        return Double.parseDouble(rateString);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CurrencyConverterGUI());
    }
}