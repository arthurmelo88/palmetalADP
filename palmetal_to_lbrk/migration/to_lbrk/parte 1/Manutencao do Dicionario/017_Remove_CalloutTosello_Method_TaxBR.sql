--	Remove a CalloutTaxConfiguration
UPDATE AD_Column SET Callout=REPLACE (Callout, ';org.tosello.callout.CalloutTosello.taxBR', '') WHERE Callout LIKE '%CalloutTosello%'
;
UPDATE AD_Column SET Callout=REPLACE (Callout, 'org.tosello.callout.CalloutTosello.taxBR;', '') WHERE Callout LIKE '%CalloutTosello%'
;
UPDATE AD_Column SET Callout=REPLACE (Callout, 'org.tosello.callout.CalloutTosello.taxBR', '') WHERE Callout LIKE '%CalloutTosello%'
;