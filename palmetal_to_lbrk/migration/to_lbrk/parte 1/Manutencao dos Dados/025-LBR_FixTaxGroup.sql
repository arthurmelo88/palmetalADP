-- Criar backup da tabela
CREATE TABLE OLD_LBR_TaxGroup AS (SELECT * FROM LBR_TaxGroup);
DELETE FROM LBR_TaxGroup;

INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120000, 0, 0, 'Y', '2012-06-22 11:25:19.0', 100, '2012-06-22 11:25:19.0', 100, 'ICMS', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120001, 0, 0, 'Y', '2012-06-22 11:26:45.0', 100, '2012-06-22 11:26:45.0', 100, 'PIS', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120002, 0, 0, 'Y', '2012-06-22 11:26:51.0', 100, '2012-06-22 11:26:51.0', 100, 'COFINS', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120003, 0, 0, 'Y', '2012-06-22 11:27:01.0', 100, '2012-06-22 11:27:01.0', 100, 'IPI', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120004, 0, 0, 'Y', '2012-06-22 11:27:13.0', 100, '2012-06-22 11:27:13.0', 100, 'II', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120006, 0, 0, 'Y', '2012-06-22 11:28:16.0', 100, '2012-06-22 11:28:16.0', 100, 'IR', NULL);
INSERT INTO LBR_TaxGroup(lbr_taxgroup_id, ad_client_id, ad_org_id, isactive, created, createdby, updated, updatedby, name, description)
  VALUES(1120007, 0, 0, 'Y', '2012-06-22 11:42:31.0', 100, '2012-06-22 11:42:31.0', 100, 'ICMSST', NULL);

-- ICMS
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120000 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120000 AND Name LIKE '%ICMS%' AND Name not LIKE '%ST%')
;
-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120000 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120000 AND Name LIKE '%ICMS%' AND Name not LIKE '%ST%')
;
-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120000 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120000 AND Name LIKE '%ICMS%' AND Name not LIKE '%ST%')
;
-- Tabela LBR_TaxGroup
DELETE  FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120000 AND Name LIKE '%ICMS%' AND Name not LIKE '%ST%'
;
-- ICMSST
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120007 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120007 AND Name LIKE '%ICMS%ST%')
;
-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120007 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120007 AND Name LIKE '%ICMS%ST%')
;
-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120007 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120007 AND Name LIKE '%ICMS%ST%')
;
-- Tabela LBR_TaxGroup
DELETE  FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120007 AND Name LIKE '%ICMS%ST%'
;
-- PIS
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120001 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120001 AND Name LIKE '%PIS%')
;
-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120001 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120001 AND Name LIKE '%PIS%')
;
-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120001 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120001 AND Name LIKE '%PIS%')
;
-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120001 AND Name LIKE '%PIS%'
;
-- COFINS
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120002 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120002 AND Name LIKE '%COFINS%')
;
-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120002 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120002 AND Name LIKE '%COFINS%')
;
-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120002 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120002 AND Name LIKE '%COFINS%')
;
-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120002 AND Name LIKE '%COFINS%'
;
-- IPI
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120003 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120003 AND Name LIKE '%IPI%');

-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120003 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120003 AND Name LIKE '%IPI%');

-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120003 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120003 AND Name LIKE '%IPI%');

-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120003 AND Name LIKE '%IPI%';

-- II
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120004 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120004 AND Name LIKE '%II%');

-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120004 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120004 AND Name LIKE '%II%');

-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120004 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120004 AND Name LIKE '%II%');

-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120004 AND Name LIKE '%II%';

-- IIS
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120005 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120005 AND Name LIKE '%IIS%');

-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120005 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120005 AND Name LIKE '%IIS%');

-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120005 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120005 AND Name LIKE '%IIS%');

-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120005 AND Name LIKE '%IIS%';

-- IR
-- Tabela C_Tax
UPDATE C_Tax SET LBR_TaxGroup_ID=1120006 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120006 AND Name LIKE '%IR%');

-- Tabela LBR_NFTax
UPDATE LBR_NFTax SET LBR_TaxGroup_ID=1120006 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120006 AND Name LIKE '%IR%');

-- Tabela LBR_NFLINeTax
UPDATE LBR_NFLINeTax SET LBR_TaxGroup_ID=1120006 WHERE LBR_TaxGroup_ID IN 
(SELECT LBR_TaxGroup_ID FROM OLD_LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120006 AND Name LIKE '%IR%');

-- Tabela LBR_TaxGroup
DELETE FROM LBR_TaxGroup WHERE LBR_TaxGroup_ID<>1120006 AND Name LIKE '%IR%';


