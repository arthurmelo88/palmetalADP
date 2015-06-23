-- 27/03/2013 10h2min53s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
INSERT INTO AD_SysConfig (AD_Client_ID,AD_Org_ID,AD_SysConfig_ID,ConfigurationLevel,Created,CreatedBy,EntityType,IsActive,Name,Updated,UpdatedBy,Value) VALUES (0,0,2000000,'S',TO_TIMESTAMP('2013-03-27 10:02:53','YYYY-MM-DD HH24:MI:SS'),100,'U','Y','LBR_RECALCULATE_TAXES_ON_COMPLETE',TO_TIMESTAMP('2013-03-27 10:02:53','YYYY-MM-DD HH24:MI:SS'),100,'true')
;

-- 27/03/2013 10h3min14s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_SysConfig SET Value='Y',Updated=TO_TIMESTAMP('2013-03-27 10:03:14','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_SysConfig_ID=2000000
;

-- 27/03/2013 10h3min25s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_SysConfig SET ConfigurationLevel='C',Updated=TO_TIMESTAMP('2013-03-27 10:03:25','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_SysConfig_ID=2000000
;

-- 27/03/2013 10h27min17s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_SysConfig SET Value='N',Updated=TO_TIMESTAMP('2013-03-27 10:27:17','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_SysConfig_ID=2000000
;

-- 27/03/2013 11h28min55s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET Callout=NULL,Updated=TO_TIMESTAMP('2013-03-27 11:28:55','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=2196
;

