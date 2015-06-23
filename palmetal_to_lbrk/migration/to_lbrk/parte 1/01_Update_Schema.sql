
ALTER TABLE LBR_TaxLine ADD Column IsTaxIncluded character(1) NOT NULL DEFAULT 'N';
ALTER TABLE LBR_TaxLine ADD Column LBR_LegalMessage_ID numeric(10,0) DEFAULT NULL;
ALTER TABLE LBR_TaxLine ADD Column LBR_TaxStatus_ID numeric(10,0) DEFAULT NULL;

ALTER TABLE LBR_NFLineTax ADD Column LBR_LegalMessage_ID numeric(10,0) DEFAULT NULL;
ALTER TABLE LBR_NFLineTax ADD Column LBR_TaxStatus_ID numeric(10,0) DEFAULT NULL;


Alter Table LBR_TaxFormula ADD Column lbr_formulaadd_id numeric(10,0) DEFAULT NULL;
Alter Table LBR_TaxFormula ADD Column lbr_formulabase_id numeric(10,0) DEFAULT NULL;
Alter Table LBR_TaxFormula ADD Column lbr_formulanet_id numeric(10,0) DEFAULT NULL;
Alter Table LBR_TaxFormula ADD Column lbr_formula_id numeric(10,0) DEFAULT NULL;
Alter Table LBR_TaxFormula ADD Column validfrom timestamp without time zone;
Alter Table LBR_TaxFormula ADD Column istaxincluded character(1) NOT NULL DEFAULT 'N';

Alter Table LBR_NCM ADD Column lbr_HasSubstitution character(1) NOT NULL DEFAULT 'N';

Alter Table LBR_ICMSMatrix ADD Column LBR_STTax_ID  numeric(10,0) DEFAULT NULL;
Alter Table LBR_ICMSMatrix ADD Column Description character varying(255) DEFAULT NULL;
Alter Table LBR_ICMSMatrix ADD Column ValidFrom timestamp without time zone;

-- 04/10/2011 16h56min25s BRT
-- Adicionar a coluna de verificacao se o imposto deve ser recalculado
ALTER TABLE C_OrderLine ADD COLUMN lbr_RecalculateTax character(1) DEFAULT 'Y' NOT NULL
;
-- 04/10/2011 16h56min25s BRT
-- Adicionar a coluna do frete na linha da fatura
ALTER TABLE C_InvoiceLine ADD COLUMN FreightAmt NUMERIC DEFAULT '0' NOT NULL
;
-- 04/10/2011 16h56min25s BRT
-- Adicionar a coluna de verificacao se o imposto deve ser recalculado
ALTER TABLE C_InvoiceLine ADD COLUMN lbr_RecalculateTax character(1) DEFAULT 'Y' NOT NULL
;

-- Table: adempiere.lbr_formula
-- DROP TABLE adempiere.lbr_formula;

CREATE TABLE adempiere.lbr_formula
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  description character varying(512) DEFAULT NULL,
  isactive character(1) NOT NULL DEFAULT 'Y' CHECK (isactive IN ('Y', 'N')),
  lbr_formula_id numeric(10,0) PRIMARY KEY NOT NULL,
  name character varying(40) NOT NULL,
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  lbr_formula character varying(512) NOT NULL
);


-- Table: adempiere.lbr_ncmtax

-- DROP TABLE adempiere.lbr_ncmtax;

CREATE TABLE adempiere.lbr_ncmtax
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  description character varying(255) DEFAULT NULL ,
  isactive character(1) NOT NULL DEFAULT 'Y' CHECK (isactive IN ('Y', 'N')),
  lbr_ncmtax_id numeric(10,0) PRIMARY KEY NOT NULL,
  lbr_ncm_id numeric(10,0) NOT NULL,
  lbr_tax_id numeric(10,0) DEFAULT NULL,
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  validfrom timestamp without time zone,
  c_region_id numeric(10,0) DEFAULT NULL::numeric,
  lbr_hassubstitution character(1) NOT NULL DEFAULT 'N' CHECK (lbr_hassubstitution IN ('Y', 'N'))
 );

-- Table: adempiere.lbr_taxstatus

-- DROP TABLE adempiere.lbr_taxstatus;

CREATE TABLE adempiere.lbr_taxstatus
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  created timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  description character varying(255) NOT NULL DEFAULT NULL,
  isactive character(1) NOT NULL CHECK (isactive IN ('Y', 'N')),
  lbr_taxname_id numeric(10,0) NOT NULL,
  lbr_taxstatus_id numeric(10,0)  PRIMARY KEY NOT NULL,
  name character varying(60) NOT NULL,
  po_description character varying(255) DEFAULT NULL,
  po_name character varying(60) DEFAULT NULL,
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  validfrom timestamp without time zone NOT NULL,
  validto timestamp without time zone,
  script character varying(2000) DEFAULT NULL
);

-- Table: adempiere.lbr_taxdefinition

-- DROP TABLE adempiere.lbr_taxdefinition;

CREATE TABLE adempiere.lbr_taxdefinition
(
  ad_client_id numeric(10,0) NOT NULL,
  ad_org_id numeric(10,0) NOT NULL,
  c_bpartner_id numeric(10,0) DEFAULT NULL,
  c_doctype_id numeric(10,0) DEFAULT NULL,
  c_region_id numeric(10,0) DEFAULT NULL,
  created timestamp without time zone NOT NULL,
  createdby numeric(10,0) NOT NULL,
  description character varying(255) DEFAULT NULL,
  isactive character(1) NOT NULL CHECK (isactive IN ('Y', 'N')),
  lbr_bpartnercategory_id numeric(10,0) DEFAULT NULL,
  lbr_fiscalgroup_bpartner_id numeric(10,0) DEFAULT NULL,
  lbr_fiscalgroup_product_id numeric(10,0) DEFAULT NULL,
  lbr_legalmessage_id numeric(10,0) DEFAULT NULL,
  lbr_ncm_id numeric(10,0) DEFAULT NULL,
  lbr_productcategory_id numeric(10,0) DEFAULT NULL,
  lbr_taxdefinition_id numeric(10,0) NOT NULL PRIMARY KEY,
  lbr_tax_id numeric(10,0) DEFAULT NULL,
  m_product_id numeric(10,0) DEFAULT NULL,
  to_region_id numeric(10,0) DEFAULT NULL,
  updated timestamp without time zone NOT NULL,
  updatedby numeric(10,0) NOT NULL,
  lbr_issubtributaria character(1) NOT NULL DEFAULT 'B',
  lbr_taxstatus character varying(2) DEFAULT NULL,
  lbr_transactiontype character varying(3) DEFAULT NULL,
  validfrom timestamp without time zone NOT NULL,
  priorityno numeric(10,0) DEFAULT NULL,
  issotrx character(1) NOT NULL DEFAULT 'B',
  lbr_cfop_id numeric(10,0) DEFAULT NULL
);
