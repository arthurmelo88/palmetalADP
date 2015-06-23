
update M_Product po
set LBR_NCM_ID = (
	Select ncm1.LBR_NCM_ID 
	From LBR_NCM ncm1 INNER JOIN OLD_LBR_NCM ncm2 on (ncm1.value=ncm2.value)
	Where ncm2.ad_client_id=2000000 
		AND po.LBR_NCM_ID=ncm2.LBR_NCM_ID
	)
where LBR_NCM_ID NOT IN (SELECT LBR_NCM_ID FROM LBR_NCM)
;


update LBR_NotaFiscalLine po
set LBR_NCM_ID = (
	Select ncm1.LBR_NCM_ID 
	From LBR_NCM ncm1 INNER JOIN OLD_LBR_NCM ncm2 on (ncm1.value=ncm2.value)
	Where ncm2.ad_client_id=2000000 
		AND po.LBR_NCM_ID=ncm2.LBR_NCM_ID
	)
where LBR_NCM_ID NOT IN (SELECT LBR_NCM_ID FROM LBR_NCM)
;
