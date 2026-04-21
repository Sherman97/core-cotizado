export interface QuoteLocation {
  id: number;
  locationName: string;
  city?: string;
  department?: string;
  occupancyType?: string;
  constructionType?: string;
  riskZoneCode?: string;
  insuredValue: number;
  validationStatus: 'VALID' | 'INCOMPLETE' | 'INVALID';
  alerts: string[];
}
