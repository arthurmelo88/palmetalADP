-- 21/04/2013 14h12min31s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
ALTER TABLE LBR_NotaFiscal ADD COLUMN lbr_MotivoCancel VARCHAR(255) DEFAULT NULL 
;


-- 22/04/2013 0h23min46s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO LBR_NFeWebService (AD_Client_ID,AD_Org_ID,C_Region_ID,Created,CreatedBy,IsActive,LBR_NFeWebService_ID,Name,URL,Updated,UpdatedBy,VersionNo,lbr_NFeEnv) VALUES (0,0,459,TO_TIMESTAMP('2013-04-22 00:23:46','YYYY-MM-DD HH24:MI:SS'),100,'Y',1120023,'NfeRecepcao','https://homologacao.nfe.sefazvirtual.rs.gov.br/ws/recepcaoevento/recepcaoevento.asmx',TO_TIMESTAMP('2013-04-22 00:23:46','YYYY-MM-DD HH24:MI:SS'),100,'2.00','2')
;

-- 22/04/2013 0h24min5s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO LBR_NFeWebService (AD_Client_ID,AD_Org_ID,C_Region_ID,Created,CreatedBy,IsActive,LBR_NFeWebService_ID,Name,URL,Updated,UpdatedBy,VersionNo,lbr_NFeEnv) VALUES (0,0,459,TO_TIMESTAMP('2013-04-22 00:24:05','YYYY-MM-DD HH24:MI:SS'),100,'Y',1120024,'NfeRetRecepcao','https://homologacao.nfe.sefazvirtual.rs.gov.br/ws/NfeRetRecepcao/NfeRetRecepcao2.asmx',TO_TIMESTAMP('2013-04-22 00:24:05','YYYY-MM-DD HH24:MI:SS'),100,'2.00','2')
;

-- 22/04/2013 0h24min27s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO LBR_NFeWebService (AD_Client_ID,AD_Org_ID,C_Region_ID,Created,CreatedBy,IsActive,LBR_NFeWebService_ID,Name,URL,Updated,UpdatedBy,VersionNo,lbr_NFeEnv) VALUES (0,0,459,TO_TIMESTAMP('2013-04-22 00:24:27','YYYY-MM-DD HH24:MI:SS'),100,'Y',1120025,'NfeCancelamento','https://homologacao.nfe.sefazvirtual.rs.gov.br/ws/NfeRetRecepcao/NfeRetRecepcao2.asmx',TO_TIMESTAMP('2013-04-22 00:24:27','YYYY-MM-DD HH24:MI:SS'),100,'2.00','2')
;

-- 22/04/2013 0h25min4s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO LBR_NFeWebService (AD_Client_ID,AD_Org_ID,C_Region_ID,Created,CreatedBy,IsActive,LBR_NFeWebService_ID,Name,URL,Updated,UpdatedBy,VersionNo,lbr_NFeEnv) VALUES (0,0,459,TO_TIMESTAMP('2013-04-22 00:25:04','YYYY-MM-DD HH24:MI:SS'),100,'Y',1120026,'NfeInutilizacao','https://homologacao.nfe.sefazvirtual.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao2.asmx',TO_TIMESTAMP('2013-04-22 00:25:04','YYYY-MM-DD HH24:MI:SS'),100,'2.00','2')
;

-- 22/04/2013 0h33min1s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE LBR_NFeWebService SET URL='https://homologacao.nfe.sefazvirtual.rs.gov.br/ws/Nferecepcao/NFeRecepcao2.asmx',Updated=TO_TIMESTAMP('2013-04-22 00:33:01','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE LBR_NFeWebService_ID=1120023
;

