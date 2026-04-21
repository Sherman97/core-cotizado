export interface CalculationResult {
  netPremium: number;
  expenseAmount: number;
  taxAmount: number;
  totalPremium: number;
  locations: Array<{
    locationId: number;
    locationName: string;
    status: string;
    premium: number;
    alerts: string[];
  }>;
  alerts: string[];
}
