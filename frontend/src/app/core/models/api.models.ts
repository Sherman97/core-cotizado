/**
 * API Response Envelope
 */
export interface ApiResponse<T> {
  data: T;
  success?: boolean;
  message?: string;
  timestamp?: string;
}

/**
 * Quotation / Folio Models
 */
export interface CreateFolioResponse {
  numeroFolio: string;
  createdAt?: string;
  estadoCotizacion?: string;
  version?: number;
}

export interface QuoteListItem {
  folio: string;
  customerName?: string;
  totalInsuredValue: number;
  totalLocations: number;
  status: string;
  createdAt?: string;
  totalPremium?: number | null;
}

export interface GeneralInfo {
  productCode: string;
  customerName: string;
  currency: string;
  agentCode?: string;
  agentNameSnapshot?: string;
  riskClassification?: string;
  businessType?: string;
  observations?: string;
  businessVersion?: number;
  modifiedAt?: string;
}

export interface Agent {
  agentCode: string;
  agentName: string;
  channel: string;
  branch: string;
  active: boolean;
}

/**
 * Location Layout Models
 */
export interface LocationLayout {
  expectedLocationCount: number;
  captureRiskZone: boolean;
  captureGeoreference: boolean;
  notes?: string;
  businessVersion?: number;
  modifiedAt?: string;
}

/**
 * Location Models
 */
export interface Location {
  locationName: string;
  city: string;
  colony?: string;
  municipality?: string;
  department: string;
  address?: string;
  postalCode?: string;
  constructionType: string;
  constructionLevel?: number;
  constructionYear?: number;
  occupancyType: string;
  fireKey?: string;
  catastrophicZone?: boolean;
  insuredValue: number;
  indice?: number;
  guarantees?: string[];
}

export interface LocationsPayload {
  locations: Location[];
}

export interface LocationPatchPayload {
  address?: string;
  colony?: string;
  municipality?: string;
  postalCode?: string;
  locationName?: string;
  city?: string;
  department?: string;
  constructionType?: string;
  constructionLevel?: number;
  constructionYear?: number;
  occupancyType?: string;
  fireKey?: string;
  catastrophicZone?: boolean;
  guarantees?: string[];
  insuredValue?: number;
}

export interface LocationsSummary {
  totalLocations: number;
  completedLocations: number;
  totalInsuredValue: number;
  businessVersion?: number;
  modifiedAt?: string;
}

export interface LocationsResult {
  locations: Location[];
  businessVersion?: number;
  modifiedAt?: string;
}

/**
 * Coverage Models
 */
export interface Coverage {
  coverageCode: string;
  coverageName: string;
  insuredLimit: number;
  deductibleType: 'FIXED' | 'PERCENTAGE';
  deductibleValue: number;
  selected: boolean;
}

export interface CoverageOptions {
  coverages: Coverage[];
  availableCoverages?: CoverageType[];
  businessVersion?: number;
  modifiedAt?: string;
}

/**
 * Calculation Models
 */
export interface CalculationResponse {
  folio: string;
  totalPremium: number;
  locationResults: LocationCalculationResult[];
  breakdownFactors?: CalculationFactors;
  warnings?: string[];
  riskScore?: number;
  calculatedAt?: string;
}

export interface LocationCalculationResult {
  indice: number;
  locationName: string;
  basePremium: number;
  appliedFactors: CalculationFactor[];
  totalPremium: number;
  calculable: boolean;
  excludionReason?: string;
}

export interface CalculationFactors {
  occupancyFactor?: number;
  zoneFactor?: number;
  constructionFactor?: number;
  riskAdjustment?: number;
  totalFactor?: number;
}

export interface CalculationFactor {
  name: string;
  value: number;
  percentage?: number;
}

/**
 * Quote State Models
 */
export interface QuoteState {
  folio: string;
  status: 'DRAFT' | 'PENDING_CALCULATION' | 'CALCULATED' | 'SAVED';
  netPremium?: number;
  expenseAmount?: number;
  taxAmount?: number;
  totalPremium?: number;
  warnings?: string[];
  businessVersion?: number;
  createdAt?: string;
  modifiedAt?: string;
}

/**
 * Catalog Models
 */
export interface OccupancyType {
  code: string;
  name: string;
  description?: string;
  fireKey?: string;
}

export interface ConstructionType {
  code: string;
  name: string;
  description?: string;
}

export interface CoverageType {
  code: string;
  name: string;
  description?: string;
  maxInsuredLimit?: number;
  active?: boolean;
}

export interface Catalog {
  occupancyTypes: OccupancyType[];
  constructionTypes: ConstructionType[];
  coverageTypes: CoverageType[];
}
