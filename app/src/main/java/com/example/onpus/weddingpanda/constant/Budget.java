package com.example.onpus.weddingpanda.constant;

/**
 * Created by onpus on 2018/1/10.
 */

public class Budget {

    private String category;
    private String subtotal;
    private String budgetsub;
    private ItemCategory itemCategory;

    public Budget(String category, String subtotal, String budgetsub, ItemCategory itemCategory) {
        this.category = category;
        this.subtotal = subtotal;
        this.budgetsub = budgetsub;
        this.itemCategory = itemCategory;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getBudgetsub() {
        return budgetsub;
    }

    public void setBudgetsub(String budgetsub) {
        this.budgetsub = budgetsub;
    }

    public ItemCategory getItemCategory() {
        return itemCategory;
    }

    public void setItemCategory(ItemCategory itemCategory) {
        this.itemCategory = itemCategory;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public static class ItemCategory{
        private String title;
        private String actualAmount;
        private String budgetAm;
        private Boolean paid;

        public ItemCategory(String title, String actualAmount, String budgetAm, Boolean paid) {
            this.title = title;
            this.actualAmount = actualAmount;
            this.budgetAm = budgetAm;
            this.paid = paid;
        }

        public ItemCategory(){

        }


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getActualAmount() {
            return actualAmount;
        }

        public void setActualAmount(String actualAmount) {
            this.actualAmount = actualAmount;
        }

        public String getBudgetAm() {
            return budgetAm;
        }

        public void setBudgetAm(String budgetAm) {
            this.budgetAm = budgetAm;
        }

        public Boolean getPaid() {
            return paid;
        }

        public void setPaid(Boolean paid) {
            this.paid = paid;
        }
    }
}
