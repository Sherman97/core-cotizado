export interface Quote {
  folio: string;
  productCode: string;
  customerName?: string;
  customerDocument?: string;
  customerEmail?: string;
  customerPhone?: string;
  policyType?: string;
  currency: string;
  insuredValue: number;
  status: string;
  currentStep?: string;
  netPremium?: number;
  expenseAmount?: number;
  taxAmount?: number;
  totalPremium?: number;
  calculationSummary?: Record<string, unknown>;
  observations?: string;
}
