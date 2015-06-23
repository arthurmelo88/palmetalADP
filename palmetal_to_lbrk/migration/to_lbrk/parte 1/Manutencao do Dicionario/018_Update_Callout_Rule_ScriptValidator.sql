-- Atualizar Callout do campo M_Product_ID da tabela C_InvoiceLine
UPDATE AD_Column
SET callout='org.compiere.model.CalloutInvoice.product;org.adempierelbr.callout.CalloutTax.getInvoiceTaxes'
WHERE AD_Column_ID=3840;

-- Desabilitar o uso do Rules
UPDATE AD_Rule
SET isActive='N'
WHERE AD_Rule_ID IN (1120000,1000000);

-- Desabilitar
UPDATE AD_Table_ScriptValidator
SET isActive='N'
where AD_Table_ScriptValidator_ID IN (2000012,2000013,2000014);