<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core"
	xmlns:java="http://xml.apache.org/xalan/java"
	xmlns:p="http://primefaces.org/ui"
	xmlns:a="urn:iso:std:iso:20022:tech:xsd:camt.054.001.02"
	>

	<xsl:output method="xml" indent="yes" />

<xsl:template match="*//*[not(child::*)]"></xsl:template>

<xsl:template match="child::*"><xsl:apply-templates/></xsl:template>

<xsl:template match="Document"  >

	<Datei>
<xsl:apply-templates/>
	</Datei>
</xsl:template>

<xsl:template match="BkToCstmrDbtCdtNtfctn" >
		<kontoauszug>
		    
            <id><xsl:value-of select="GrpHdr/MsgId"/></id>
            <gebuchtAm><xsl:value-of select="GrpHdr/CreDtTm" /></gebuchtAm>
		    
             <einzahlungen>
			<xsl:for-each select="descendant::Ntfctn">
				<xsl:apply-templates/>
			</xsl:for-each>
             </einzahlungen>		       
		</kontoauszug>

</xsl:template>

<xsl:template match="TxDtls">
    <einzahlung>
	  <xsl:apply-templates/>
	</einzahlung>
</xsl:template>

<xsl:template match="Refs/TxId">
    <transaktion><xsl:value-of select="." /></transaktion>
</xsl:template>


<xsl:template match="AmtDtls/TxAmt/Amt">
    <betrag><xsl:value-of select="." /></betrag>
</xsl:template>

<xsl:template match="RltdPties/Dbtr/Nm">
    <debitorName><xsl:value-of select="." /></debitorName>
</xsl:template>




<xsl:template match="DbtrAcct/Id/IBAN">
    <debitorIBAN><xsl:value-of select="." /></debitorIBAN>
</xsl:template>

<xsl:template match="CdtrAcct/Id/IBAN">
    <kreditorIBAN><xsl:value-of select="." /></kreditorIBAN>
</xsl:template>


<xsl:template match="RltdAgts/DbtrAgt/FinInstnId/BIC">
    <debitorBIC><xsl:value-of select="." /></debitorBIC>
</xsl:template>

<xsl:template match="RltdAgts/CdtrAgt/FinInstnId/BIC">
    <kreditorBIC><xsl:value-of select="." /></kreditorBIC>
</xsl:template>


<xsl:template match="RmtInf/Ustrd">
    <verwendungszweck><xsl:value-of select="." /></verwendungszweck>
</xsl:template>

<xsl:template match="AddtlTxInf">
     <zusatz><xsl:value-of select="."/></zusatz>
</xsl:template>


</xsl:stylesheet>

