export interface GeneralInfoFormData {
  productCode: string;
  customerName: string;
  currency: string;
  observations?: string;
}

export interface LocationFormData {
  locationName: string;
  city: string;
  department: string;
  address?: string;
  postalCode?: string;
  constructionType: string;
  occupancyType: string;
  insuredValue: number;
}

export interface CoverageFormData {
  coverageCode: string;
  coverageName: string;
  insuredLimit: number;
  deductibleType: 'FIXED' | 'PERCENTAGE';
  deductibleValue: number;
  selected: boolean;
}

export interface QuotationWizardState {
  generalInfo: GeneralInfoFormData | null;
  locations: LocationFormData[];
  coverages: CoverageFormData[];
  currentStep: number;
}

export interface QuotationSummary {
  folio: string;
  customerName: string;
  totalInsuredValue: number;
  totalLocations: number;
  status: string;
  createdAt?: Date;
  totalPremium?: number;
}

