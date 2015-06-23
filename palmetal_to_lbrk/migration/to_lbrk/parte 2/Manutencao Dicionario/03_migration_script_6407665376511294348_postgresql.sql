-- 25/03/2013 12h15min2s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Ref_List SET IsActive='Y',Updated=TO_TIMESTAMP('2013-03-25 12:15:02','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Ref_List_ID=226
;

-- 25/03/2013 12h15min5s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Ref_List SET IsActive='Y',Updated=TO_TIMESTAMP('2013-03-25 12:15:05','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Ref_List_ID=228
;

-- 25/03/2013 12h15min16s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Ref_List SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:15:16','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Ref_List_ID=1000119
;

-- 25/03/2013 12h19min35s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET DisplayLogic='@FreightCostRule@=''F''',Updated=TO_TIMESTAMP('2013-03-25 12:19:35','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=2115
;

-- 25/03/2013 12h22min26s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET DisplayLogic='@DeliveryViaRule@=''D'' & @OrderType@=''SO'' & @FreightCostRule@=''F''',Updated=TO_TIMESTAMP('2013-03-25 12:22:26','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=1107
;

-- 25/03/2013 12h24min19s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET DisplayLogic=NULL,Updated=TO_TIMESTAMP('2013-03-25 12:24:19','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=2115
;

-- 25/03/2013 12h30min43s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Table_ScriptValidator SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:30:43','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Table_ScriptValidator_ID=2000011
;

-- 25/03/2013 12h31min33s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Table_ScriptValidator SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:31:33','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Table_ScriptValidator_ID=2000030
;

-- 25/03/2013 12h36min43s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET Callout='org.compiere.model.CalloutOrder.amt;',Updated=TO_TIMESTAMP('2013-03-25 12:36:43','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=12875
;

-- 25/03/2013 12h37min12s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:37:12','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1103000
;

-- 25/03/2013 12h37min16s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:37:16','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=1103001
;

-- 25/03/2013 12h44min17s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET Callout=NULL,Updated=TO_TIMESTAMP('2013-03-25 12:44:17','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=4031
;

-- 25/03/2013 12h55min43s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET IsReadOnly='N',Updated=TO_TIMESTAMP('2013-03-25 12:55:43','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=10829
;

-- 25/03/2013 12h55min48s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET IsReadOnly='Y',Updated=TO_TIMESTAMP('2013-03-25 12:55:48','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=1138
;

-- 25/03/2013 12h58min33s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET IsActive='N',Updated=TO_TIMESTAMP('2013-03-25 12:58:33','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=2000243
;

-- 25/03/2013 12h58min53s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Column SET ColumnSQL='(PriceEntered+FreightAmt)', IsUpdateable='N',Updated=TO_TIMESTAMP('2013-03-25 12:58:53','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=2000518
;

-- 25/03/2013 12h58min59s AMT
-- I forgot to set the DICTIONARY_ID_COMMENTS System Configurator
UPDATE AD_Field SET IsActive='Y',Updated=TO_TIMESTAMP('2013-03-25 12:58:59','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=2000243
;

