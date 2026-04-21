export interface QuoteCoverage {
  id?: number;
  coverageCode: string;
  coverageName: string;
  insuredLimit?: number;
  deductibleType?: string;
  deductibleValue?: number;
  baseRate?: number;
  premiumAmount?: number;
  isSelected: boolean;
}
