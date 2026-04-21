import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { QuoteApiService } from '../../../core/services/quote-api.service';

@Component({
  selector: 'app-quotes-list-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="quotes-list-container">
      <div class="list-header">
        <h2>Quotations</h2>
        <button class="btn btn-primary" (click)="createNewQuote()">
          + Create New Quotation
        </button>
      </div>

      <div *ngIf="loading" class="loading">
        Loading quotations...
      </div>

      <div *ngIf="!loading && quotations.length === 0" class="empty-state">
        <p>No quotations yet. Create your first quotation to get started.</p>
      </div>

      <div *ngIf="!loading && quotations.length > 0" class="table-container">
        <table class="quotations-table">
          <thead>
            <tr>
              <th>Folio</th>
              <th>Customer</th>
              <th>Insured Value</th>
              <th>Locations</th>
              <th>Status</th>
              <th>Created</th>
              <th>Premium</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let quote of quotations">
              <td>{{ quote.folio }}</td>
              <td>{{ quote.customerName }}</td>
              <td>{{ quote.totalInsuredValue | currency }}</td>
              <td>{{ quote.totalLocations }}</td>
              <td>
                <span [class]="'status status-' + quote.status.toLowerCase()">
                  {{ quote.status }}
                </span>
              </td>
              <td>{{ quote.createdAt | date: 'short' }}</td>
              <td *ngIf="quote.totalPremium">{{ quote.totalPremium | currency }}</td>
              <td *ngIf="!quote.totalPremium">-</td>
              <td>
                <button class="btn btn-sm btn-info" (click)="viewDetails(quote.folio)">
                  View
                </button>
                <button class="btn btn-sm btn-warning" (click)="editQuote(quote.folio)">
                  Edit
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .quotes-list-container {
      padding: 2rem;
    }

    .list-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
    }

    .list-header h2 {
      margin: 0;
      font-size: 2rem;
      color: #2c3e50;
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

    .btn-sm {
      padding: 0.5rem 1rem;
      font-size: 0.875rem;
    }

    .btn-info {
      background-color: #17a2b8;
      color: white;
    }

    .btn-info:hover {
      background-color: #138496;
    }

    .btn-warning {
      background-color: #ffc107;
      color: black;
    }

    .btn-warning:hover {
      background-color: #e0a800;
    }

    .loading {
      text-align: center;
      padding: 2rem;
      font-size: 1.1rem;
      color: #7f8c8d;
    }

    .empty-state {
      text-align: center;
      padding: 4rem 2rem;
      background-color: #f8f9fa;
      border-radius: 8px;
      color: #7f8c8d;
    }

    .table-container {
      overflow-x: auto;
    }

    .quotations-table {
      width: 100%;
      border-collapse: collapse;
      margin-top: 1rem;
    }

    .quotations-table thead {
      background-color: #ecf0f1;
    }

    .quotations-table th,
    .quotations-table td {
      padding: 1rem;
      text-align: left;
      border-bottom: 1px solid #bdc3c7;
    }

    .quotations-table tbody tr:hover {
      background-color: #f8f9fa;
    }

    .status {
      display: inline-block;
      padding: 0.5rem 1rem;
      border-radius: 20px;
      font-size: 0.875rem;
      font-weight: 500;
    }

    .status-draft {
      background-color: #e8f4f8;
      color: #2c3e50;
    }

    .status-pending_calculation {
      background-color: #fff3cd;
      color: #856404;
    }

    .status-calculated {
      background-color: #d4edda;
      color: #155724;
    }

    .status-saved {
      background-color: #cfe2ff;
      color: #084298;
    }

    @media (max-width: 1024px) {
      .list-header {
        flex-direction: column;
        gap: 1rem;
        align-items: flex-start;
      }

      .quotations-table th,
      .quotations-table td {
        padding: 0.75rem;
        font-size: 0.875rem;
      }
    }
  `]
})
export class QuotesListPageComponent implements OnInit {
  quotations: any[] = [];
  loading: boolean = true;

  constructor(
    private router: Router,
    private quoteApi: QuoteApiService
  ) {}

  ngOnInit(): void {
    this.loadQuotations();
  }

  /**
   * Load quotations list (mock for now - backend API needed)
   */
  loadQuotations(): void {
    this.loading = true;
    // Mock data - Replace with actual API call
    setTimeout(() => {
      this.quotations = [
        {
          folio: 'FOL-2024-001',
          customerName: 'Comercial Andina SAS',
          totalInsuredValue: 2400000,
          totalLocations: 2,
          status: 'CALCULATED',
          createdAt: new Date('2024-01-15'),
          totalPremium: 45000
        },
        {
          folio: 'FOL-2024-002',
          customerName: 'Retail Store Inc',
          totalInsuredValue: 1500000,
          totalLocations: 1,
          status: 'DRAFT',
          createdAt: new Date('2024-01-18'),
          totalPremium: null
        }
      ];
      this.loading = false;
    }, 500);
  }

  /**
   * Create a new quotation
   */
  createNewQuote(): void {
    this.quoteApi.createFolio().subscribe({
      next: (response) => {
        const folio = response.data.numeroFolio;
        this.router.navigate(['/quotes', folio, 'wizard']);
      },
      error: (err) => {
        console.error('Error creating folio:', err);
      }
    });
  }

  /**
   * View quotation details
   */
  viewDetails(folio: string): void {
    this.router.navigate(['/quotes', folio, 'detail']);
  }

  /**
   * Edit existing quotation
   */
  editQuote(folio: string): void {
    this.router.navigate(['/quotes', folio, 'wizard']);
  }
}

