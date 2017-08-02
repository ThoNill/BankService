<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:h="http://java.sun.com/jsf/html"
	xmlns:f="http://java.sun.com/jsf/core" xmlns:java="http://xml.apache.org/xalan/java"
	xmlns:p="http://primefaces.org/ui" xmlns:a="urn:iso:std:iso:20022:tech:xsd:camt.054.001.02">
	<xsl:template match="ueberweisungen">

		<Document xmlns="urn:iso:std:iso:20022:tech:xsd:pain.001.003.03"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xsi:schemaLocation="urn:iso:std:iso:20022:tech:xsd:pain.001.003.03 pain.001.003.03.xsd">
			<CstmrCdtTrfInitn>
				<GrpHdr>
					<MsgId>
						Message-ID-
						<xsl:value-of select="id" />
					</MsgId>
					<CreDtTm><xsl:value-of select="datum" /></CreDtTm>
					<NbOfTxs> <xsl:value-of select="count(auftrag)" /> </NbOfTxs>
					<InitgPty>
						<Nm>
							<xsl:value-of select="name" />
						</Nm>
					</InitgPty>
				</GrpHdr>

				<xsl:apply-templates select="auftrag"/>


			</CstmrCdtTrfInitn>
		</Document>

	</xsl:template>

	<xsl:template match="auftrag">

		<PmtInf>
			<PmtInfId>
				Payment-ID-
				<xsl:value-of select="id" />
			</PmtInfId>
			<PmtMtd>TRF</PmtMtd>
			<BtchBookg>true</BtchBookg>
			<NbOfTxs> <xsl:value-of select="count(ueberweisung)" /></NbOfTxs>
			<CtrlSum><xsl:value-of select="sum(ueberweisung/betrag)" /></CtrlSum>
			<PmtTpInf>
				<SvcLvl>
					<Cd>SEPA</Cd>
				</SvcLvl>
			</PmtTpInf>
			<ReqdExctnDt>
				<xsl:value-of select="datum" /></ReqdExctnDt>
			<Dbtr>
				<Nm>
					<xsl:value-of select="name" />
				</Nm>
			</Dbtr>
			<DbtrAcct>
				<Id>
					<IBAN>
						<xsl:value-of select="iban" />
					</IBAN>
				</Id>
			</DbtrAcct>
			<DbtrAgt>
				<FinInstnId>
					<BIC>
						<xsl:value-of select="bic" />
					</BIC>
				</FinInstnId>
			</DbtrAgt>
			<ChrgBr>SLEV</ChrgBr>

			<xsl:apply-templates select="ueberweisung"/>

		</PmtInf>
	</xsl:template>

	<xsl:template match="ueberweisung">

		<CdtTrfTxInf>
			<PmtId>
				<EndToEndId>
					<xsl:value-of select="id" />
				</EndToEndId>
			</PmtId>
			<Amt>
				<InstdAmt Ccy="EUR">
					<xsl:value-of select="betrag" />
				</InstdAmt>
			</Amt>
			<CdtrAgt>
				<FinInstnId>
					<BIC>
						<xsl:value-of select="bic" />
					</BIC>
				</FinInstnId>
			</CdtrAgt>
			<Cdtr>
				<Nm>
					<xsl:value-of select="name" />
				</Nm>
			</Cdtr>
			<CdtrAcct>
				<Id>
					<IBAN>
						<xsl:value-of select="iban" />
					</IBAN>
				</Id>
			</CdtrAcct>
			<RmtInf>
				<Ustrd>
					<xsl:value-of select="verwendungszweck" />
				</Ustrd>
			</RmtInf>
		</CdtTrfTxInf>

	</xsl:template>

</xsl:stylesheet>
