import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { QuoteApiService } from '../../../core/services/quote-api.service';
import { CalculationResponse, LocationCalculationResult, CalculationFactor } from '../../../core/models/api.models';

@Component({
  selector: 'app-quote-result-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="result-container">
      <div class="result-header">
        <h1>Quotation Result</h1>
        <p class="folio-info">Folio: <strong>{{ folio }}</strong></p>
      </div>

      <div *ngIf="loading" class="loading-state">
        <p>Loading quotation results...</p>
        <div class="spinner"></div>
      </div>

      <div *ngIf="errorMessage" class="error-alert">
        <strong>Error:</strong> {{ errorMessage }}
      </div>

      <div *ngIf="!loading && calculationResult" class="result-content">
        <!-- Premium Summary Card -->
        <div class="premium-card">
          <h2>Total Premium</h2>
          <div class="premium-amount">{{ calculationResult.totalPremium | currency }}</div>
          <p class="premium-description">Final quoted premium for all locations and coverages</p>
          
          <div *ngIf="calculationResult.riskScore" class="risk-score">
            <p>Risk Score: <strong>{{ calculationResult.riskScore }}/100</strong></p>
          </div>
        </div>

        <!-- Breakdown Factors -->
        <div *ngIf="calculationResult.breakdownFactors" class="factors-card">
          <h3>Calculation Factors</h3>
          <div class="factors-grid">
            <div *ngIf="calculationResult.breakdownFactors.occupancyFactor" class="factor-item">
              <span class="factor-label">Occupancy Factor:</span>
              <span class="factor-value">{{ calculationResult.breakdownFactors.occupancyFactor | number: '1.3' }}</span>
            </div>
            <div *ngIf="calculationResult.breakdownFactors.zoneFactor" class="factor-item">
              <span class="factor-label">Zone Factor:</span>
              <span class="factor-value">{{ calculationResult.breakdownFactors.zoneFactor | number: '1.3' }}</span>
            </div>
            <div *ngIf="calculationResult.breakdownFactors.constructionFactor" class="factor-item">
              <span class="factor-label">Construction Factor:</span>
              <span class="factor-value">{{ calculationResult.breakdownFactors.constructionFactor | number: '1.3' }}</span>
            </div>
            <div *ngIf="calculationResult.breakdownFactors.riskAdjustment" class="factor-item">
              <span class="factor-label">Risk Adjustment:</span>
              <span class="factor-value">{{ calculationResult.breakdownFactors.riskAdjustment | number: '1.3' }}</span>
            </div>
            <div *ngIf="calculationResult.breakdownFactors.totalFactor" class="factor-item highlight">
              <span class="factor-label">Total Factor Applied:</span>
              <span class="factor-value">{{ calculationResult.breakdownFactors.totalFactor | number: '1.3' }}</span>
            </div>
          </div>
        </div>

        <!-- Location Results -->
        <div class="locations-results-card">
          <h3>Premium by Location</h3>
          <div class="locations-table">
            <table>
              <thead>
                <tr>
                  <th>Location</th>
                  <th>Base Premium</th>
                  <th>Applied Factors</th>
                  <th>Final Premium</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let location of calculationResult.locationResults" [class.excluded]="!location.calculable">
                  <td>
                    <strong>{{ location.locationName }}</strong>
                    <br />
                    <small>Location {{ location.indice }}</small>
                  </td>
                  <td>{{ location.basePremium | currency }}</td>
                  <td>
                    <div class="factors-mini" *ngIf="location.appliedFactors && location.appliedFactors.length > 0">
                      <span *ngFor="let factor of location.appliedFactors" class="factor-badge">
                        {{ factor.name }}: {{ factor.value | number: '1.2' }}
                        <span *ngIf="factor.percentage"> ({{ factor.percentage }}%)</span>
                      </span>
                    </div>
                    <div *ngIf="!location.calculable" class="excluded-badge">
                      Not calculable
                    </div>
                  </td>
                  <td>
                    <span *ngIf="location.calculable" class="premium-value">
                      {{ location.totalPremium | currency }}
                    </span>
                    <span *ngIf="!location.calculable" class="zero-value">-</span>
                  </td>
                  <td>
                    <span *ngIf="location.calculable" class="status-badge calculable">✓ Calculable</span>
                    <span *ngIf="!location.calculable" class="status-badge excluded">✗ Excluded</span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>

        <!-- Exclusion Warnings -->
        <div *ngIf="hasExclusions()" class="warnings-card">
          <h3>⚠ Exclusions & Warnings</h3>
          <div *ngFor="let location of calculationResult.locationResults" *ngIf="!location.calculable">
            <div class="warning-item">
              <strong>{{ location.locationName }}:</strong>
              {{ location.excludionReason || 'Not calculable - verify all required fields' }}
            </div>
          </div>
          <div *ngIf="calculationResult.warnings">
            <div *ngFor="let warning of calculationResult.warnings" class="warning-item">
              {{ warning }}
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="result-actions">
          <button type="button" class="btn btn-secondary" (click)="onEditQuote()">
            ← Edit Quotation
          </button>
          <button type="button" class="btn btn-primary" (click)="onSaveQuote()">
            💾 Save Quotation
          </button>
          <button type="button" class="btn btn-success" (click)="onPrint()">
            🖨️ Print / Export
          </button>
        </div>
      </div>

      <div *ngIf="!loading && !calculationResult" class="empty-result">
        <p>No calculation result available. Please create a new quotation.</p>
        <button type="button" class="btn btn-primary" (click)="onBackToList()">
          ← Back to Quotations
        </button>
      </div>
    </div>
  `,
  styles: [`
    .result-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 2rem;
    }

    .result-header {
      text-align: center;
      margin-bottom: 2rem;
    }

    .result-header h1 {
      margin: 0 0 0.5rem 0;
      color: #2c3e50;
      font-size: 2rem;
    }

    .folio-info {
      margin: 0;
      color: #7f8c8d;
      font-size: 0.95rem;
    }

    .loading-state {
      text-align: center;
      padding: 3rem;
      background-color: white;
      border-radius: 8px;
    }

    .loading-state p {
      margin-bottom: 1rem;
      color: #2c3e50;
      font-size: 1.1rem;
    }

    .spinner {
      width: 40px;
      height: 40px;
      margin: 0 auto;
      border: 4px solid #ecf0f1;
      border-top-color: #3498db;
      border-radius: 50%;
      animation: spin 1s linear infinite;
    }

    @keyframes spin {
      to {
        transform: rotate(360deg);
      }
    }

    .error-alert {
      background-color: #f8d7da;
      border: 1px solid #f5c6cb;
      border-radius: 4px;
      padding: 1rem;
      margin-bottom: 1rem;
      color: #721c24;
    }

    .result-content {
      animation: fadeIn 0.3s ease-in;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .premium-card {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 2rem;
      border-radius: 8px;
      text-align: center;
      margin-bottom: 2rem;
      box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }

    .premium-card h2 {
      margin: 0 0 1rem 0;
      font-size: 1.2rem;
      opacity: 0.9;
    }

    .premium-amount {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 0.5rem;
    }

    .premium-description {
      margin: 0;
      font-size: 0.95rem;
      opacity: 0.9;
    }

    .risk-score {
      margin-top: 1rem;
      padding-top: 1rem;
      border-top: 1px solid rgba(255,255,255,0.2);
    }

    .risk-score p {
      margin: 0;
    }

    .factors-card,
    .locations-results-card,
    .warnings-card {
      background-color: white;
      border: 1px solid #e0e0e0;
      border-radius: 8px;
      padding: 1.5rem;
      margin-bottom: 1.5rem;
    }

    .factors-card h3,
    .locations-results-card h3,
    .warnings-card h3 {
      margin-top: 0;
      color: #2c3e50;
      border-bottom: 2px solid #ecf0f1;
      padding-bottom: 1rem;
    }

    .factors-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 1rem;
    }

    .factor-item {
      background-color: #f8f9fa;
      padding: 1rem;
      border-radius: 4px;
      border-left: 4px solid #3498db;
    }

    .factor-item.highlight {
      background-color: #fff9e6;
      border-left-color: #ffc107;
    }

    .factor-label {
      display: block;
      color: #7f8c8d;
      font-weight: 500;
      margin-bottom: 0.5rem;
    }

    .factor-value {
      display: block;
      font-size: 1.5rem;
      color: #2c3e50;
      font-weight: 700;
    }

    .locations-table {
      overflow-x: auto;
    }

    .locations-table table {
      width: 100%;
      border-collapse: collapse;
    }

    .locations-table th,
    .locations-table td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #ecf0f1;
    }

    .locations-table th {
      background-color: #f8f9fa;
      font-weight: 600;
      color: #2c3e50;
    }

    .locations-table tbody tr:hover {
      background-color: #f8f9fa;
    }

    .locations-table tbody tr.excluded {
      opacity: 0.6;
      background-color: #f8d7da;
    }

    .factors-mini {
      display: flex;
      flex-direction: column;
      gap: 0.25rem;
    }

    .factor-badge {
      background-color: #e3f2fd;
      color: #1976d2;
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .excluded-badge {
      background-color: #f8d7da;
      color: #721c24;
      padding: 0.25rem 0.75rem;
      border-radius: 12px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .premium-value {
      color: #27ae60;
      font-weight: 700;
      font-size: 1.1rem;
    }

    .zero-value {
      color: #7f8c8d;
    }

    .status-badge {
      display: inline-block;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      font-size: 0.9rem;
      font-weight: 600;
    }

    .status-badge.calculable {
      background-color: #d4edda;
      color: #155724;
    }

    .status-badge.excluded {
      background-color: #f8d7da;
      color: #721c24;
    }

    .warnings-card {
      border-left: 4px solid #ffc107;
      background-color: #fffbf0;
    }

    .warning-item {
      padding: 1rem;
      background-color: white;
      border-radius: 4px;
      margin-bottom: 0.5rem;
      border-left: 3px solid #ffc107;
    }

    .result-actions {
      display: flex;
      justify-content: center;
      gap: 1rem;
      margin-top: 2rem;
      padding-top: 2rem;
      border-top: 1px solid #ddd;
      flex-wrap: wrap;
    }

    .btn {
      padding: 0.75rem 1.5rem;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 1rem;
      font-weight: 500;
      transition: all 0.3s;
    }

    .btn-primary {
      background-color: #3498db;
      color: white;
    }

    .btn-primary:hover {
      background-color: #2980b9;
    }

    .btn-secondary {
      background-color: #95a5a6;
      color: white;
    }

    .btn-secondary:hover {
      background-color: #7f8c8d;
    }

    .btn-success {
      background-color: #27ae60;
      color: white;
    }

    .btn-success:hover {
      background-color: #229954;
    }

    .empty-result {
      text-align: center;
      padding: 3rem;
      background-color: white;
      border-radius: 8px;
      color: #7f8c8d;
    }

    @media (max-width: 768px) {
      .result-container {
        padding: 1rem;
      }

      .premium-amount {
        font-size: 2rem;
      }

      .factors-grid {
        grid-template-columns: 1fr;
      }

      .locations-table table {
        font-size: 0.9rem;
      }

      .locations-table th,
      .locations-table td {
        padding: 0.75rem;
      }

      .result-actions {
        flex-direction: column;
      }

      .result-actions button {
        width: 100%;
      }
    }
  `]
})
export class QuoteResultPageComponent implements OnInit {
  folio: string = '';
  calculationResult: CalculationResponse | null = null;
  loading: boolean = true;
  errorMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private quoteApi: QuoteApiService
  ) {}

  ngOnInit(): void {
    this.folio = this.route.snapshot.paramMap.get('folio') || '';
    this.loadQuoteState();
  }

  /**
   * Load quote state and calculation result
   */
  private loadQuoteState(): void {
    this.quoteApi.getQuoteState(this.folio).subscribe({
      next: (response) => {
        if (response.data && response.data.calculationResult) {
          this.calculationResult = response.data.calculationResult;
        }
        this.loading = false;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = 'Error loading quotation: ' + (err.error?.message || err.message);
        console.error('Error loading quote:', err);
      }
    });
  }

  /**
   * Check if there are exclusions
   */
  hasExclusions(): boolean {
    if (!this.calculationResult) return false;
    return (
      this.calculationResult.locationResults.some(loc => !loc.calculable) ||
      (this.calculationResult.warnings && this.calculationResult.warnings.length > 0)
    );
  }

  /**
   * Edit quotation
   */
  onEditQuote(): void {
    this.router.navigate(['/quotes', this.folio, 'wizard']);
  }

  /**
   * Save quotation
   */
  onSaveQuote(): void {
    // TODO: Implement save functionality
    alert('Quote saved successfully!');
  }

  /**
   * Print or export
   */
  onPrint(): void {
    window.print();
  }

  /**
   * Back to list
   */
  onBackToList(): void {
    this.router.navigate(['/']);
  }
}

