package com.example.paragony;


import android.content.Context;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;


public class DataBaseModel {

    public Context context;

    public ReceiptSchema receipt;

    public List<ItemSchema> itemsList;

    public int[] TAX_VALUES = {0, 0, 0, 0, 0, 0, 0};

    private List<String[]> categoryNames;



    public DataBaseModel(Context context, List<List<Paragraph_schema>> lines) {

        this.context = context;

        categoryNames = Arrays.asList(new String[][]
                {{"address", context.getString(R.string.address)},
                        {"currency",  context.getString(R.string.currency)},
                        {"date",  context.getString(R.string.date)},
                        {"item",  context.getString(R.string.item)},
                        {"nip",  context.getString(R.string.nip)},
                        {"per_unit",  context.getString(R.string.per_unit)},
                        {"price",  context.getString(R.string.price)},
                        {"quantity",  context.getString(R.string.quantity)},
                        {"receipt_id",  context.getString(R.string.receipt_id)},
                        {"shop",  context.getString(R.string.shop)},
                        {"total",  context.getString(R.string.total)},
                        {"total_tax",  context.getString(R.string.total_tax)},
                        {"", ""}}
        );

        translateCategories(lines);

        ParagonyDatabaseTools paragonyDatabaseTools = new ParagonyDatabaseTools(context);

        List<Integer> taxValues = paragonyDatabaseTools.getTaxValues();

        for (int i = 0; i < taxValues.size(); i++) {

            TAX_VALUES[i] = taxValues.get(i);

        }


        ReceiptSchema receiptSchema;

        List<Paragraph_schema> itemsLine;

        Paragraph_schema itemsCell;

        String address = "";

        itemsList = new ArrayList<>();

        ItemSchema itemSchema;

        receiptSchema = new ReceiptSchema();

        for (int i = 0; i < lines.size(); i++) {


            List<Paragraph_schema> line = lines.get(i);


            for (int j = 0; j < line.size(); j++) {

                Paragraph_schema paragraph_schema = line.get(j);


                if (paragraph_schema.category.equals("date")) {

                    receiptSchema.date = paragraph_schema.textLine.trim();
                }

                if (paragraph_schema.category.equals("shop")) {


                    receiptSchema.shop = paragraph_schema.textLine.trim();
                }

                if (paragraph_schema.category.equals("nip")) {
                    receiptSchema.nip = paragraph_schema.textLine.trim().replaceAll("\\D+", "");
                }

                if (paragraph_schema.category.equals("receipt_id")) {
                    receiptSchema.receipt_id = paragraph_schema.textLine.trim().replaceAll("\\D+", "");
                }

                if (paragraph_schema.category.equals("total")) {
                    receiptSchema.total = Double.parseDouble(comaTodDot(paragraph_schema.textLine));
                }
                if (paragraph_schema.category.equals("total_tax")) {
                    receiptSchema.total_tax = Double.parseDouble(comaTodDot(paragraph_schema.textLine));
                }

                if (paragraph_schema.category.equals("currency")) {
                    receiptSchema.currency = paragraph_schema.textLine.trim();
                }


                if (paragraph_schema.category.equals("address")) {
                    address = address + paragraph_schema.textLine.trim() + ",\n";
                }

                if (paragraph_schema.category.equals("item")) {


                    itemSchema = new ItemSchema();

                    itemSchema.item = paragraph_schema.textLine.trim().toLowerCase(Locale.ROOT);
                    itemSchema.tagsList = paragraph_schema.tagsList;
                    itemSchema.tags = tagsJson(itemSchema.tagsList);

                    itemsLine = lines.get(i);


                    itemSchema.category = paragraph_schema.category;

                    for (int l = 0; l < itemsLine.size(); l++) {

                        itemsCell = itemsLine.get(l);


                        if (itemsCell.category.equals("quantity")) {
                            itemSchema.quantity = itemsCell.textLine.trim() != "" ? Double.parseDouble(comaTodDot(itemsCell.textLine)) : 0.0;


                        }

                        if (itemsCell.category.equals("per_unit")) {
                            itemSchema.per_unit = itemsCell.textLine.trim() != "" ? Double.parseDouble(comaTodDot(itemsCell.textLine)) : 0.0;
                        }

                        if (itemsCell.category.equals("price")) {
                            String price = itemsCell.textLine.trim() != "" ? comaTodDot(itemsCell.textLine).replaceAll("[:alpha:]+", "") : "0.0";
                            itemSchema.tax_type = comaTodDot(itemsCell.textLine).replace(price, "");


                            itemSchema.price = itemSchema.per_unit * itemSchema.quantity;


                            Double pu = itemSchema.per_unit;
                            Double q = itemSchema.quantity;
                            itemSchema.tax = countTax(itemSchema.tax_type, pu,q, receiptSchema);
                            System.out.println(String.format("tax: %f", itemSchema.getTax()));

                        }
                    }

                    itemsList.add(itemSchema);
                }

                if (paragraph_schema.category.equals("total")) {
                    receiptSchema.total = Double.parseDouble(comaTodDot(paragraph_schema.textLine));
                }

                if (paragraph_schema.category.equals("total_tax")) {
                    receiptSchema.total_tax = Double.parseDouble(comaTodDot(paragraph_schema.textLine));
                }
            }
        }

        if (address.length() > 3) {

            receiptSchema.address = address.substring(0, address.length() - 2);
        }
        for (ItemSchema item : itemsList) {

            item.date = receiptSchema.date;
            item.shop = receiptSchema.shop;
            item.receipt_id = receiptSchema.receipt_id;
            item.currency = receiptSchema.currency;

        }


        receiptSchema.total_sum = itemsList.stream().map(ItemSchema::getPrice).reduce(0.0, Double::sum);
        receiptSchema.tax_sum = itemsList.stream().map(ItemSchema::getTax).reduce(0.0, Double::sum);


        receiptSchema.itemsList = itemsList;


        this.receipt = receiptSchema;


    }


    private String comaTodDot(String s) {

        return s.trim().replace(" ","")
                .replace(",", ".");

    }

    private double countTax(String tax_type, double p,double q, ReceiptSchema receipt) {


        double tax = 0.0;

        if (tax_type != null) {

            switch (tax_type) {

                case "A":

                    tax = p*q * TAX_VALUES[0] / 100.0;
                    receipt.taxA = receipt.taxA + tax;

                    break;

                case "B":

                    tax = p*q * TAX_VALUES[1] / 100.0;
                    receipt.taxB = receipt.taxB + tax;

                    break;
                case "C":

                    tax = p*q * TAX_VALUES[2] / 100.0;
                    receipt.taxC = receipt.taxC + tax;

                    break;
                case "D":

                    tax = p*q * TAX_VALUES[3] / 100.0;
                    receipt.taxD = receipt.taxD + tax;

                    break;

                case "E":

                    tax = p*q * TAX_VALUES[4] / 100.0;
                    receipt.taxE = receipt.taxE + tax;

                    break;
                case "F":

                    tax = p*q * TAX_VALUES[5] / 100.0;
                    receipt.taxF = receipt.taxF + tax;

                    break;

                case "G":

                    tax = p*q * TAX_VALUES[6] / 100.0;
                    receipt.taxG = receipt.taxG + tax;

                    break;

                default:

                    tax = 0.0;
                    receipt.taxG = 0.0;

                    break;
            }
        }

        return tax;

    }


    public String tagsJson(List<String> tags) {

        Gson gson = new Gson();
        String json;

        Tags tagsArray = new Tags(tags);

        json = gson.toJson(tagsArray);

        return json;
    }

    private static class Tags {

        public String[] tags;

        public Tags(List<String> tagsList) {

            this.tags = new String[tagsList.size()];

            for (int i = 0; i < tagsList.size(); i++) {

                this.tags[i] = tagsList.get(i);

            }
        }
    }

    private void translateCategories(List<List<Paragraph_schema>> lines) {

        for (List<Paragraph_schema> line : lines) {

            for (Paragraph_schema schema : line) {

                for (String[] categoryName : this.categoryNames) {

                    if (schema.category.matches(categoryName[1])) {

                        schema.category = categoryName[0];

                    }
                }
            }
        }
    }
}
