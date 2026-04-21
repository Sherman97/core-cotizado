import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { QuoteLocation } from '../../../../shared/models/location.model';

@Component({
  selector: 'app-locations-grid',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="grid">
      <div class="card" *ngFor="let location of locations">
        <div style="display:flex; justify-content:space-between; gap:12px; align-items:center;">
          <strong>{{ location.locationName }}</strong>
          <span class="tag"
                [ngClass]="{
                  'tag-valid': location.validationStatus === 'VALID',
                  'tag-incomplete': location.validationStatus === 'INCOMPLETE',
                  'tag-invalid': location.validationStatus === 'INVALID'
                }">
            {{ location.validationStatus }}
          </span>
        </div>
        <p>{{ location.department }} / {{ location.city }}</p>
        <p>Valor asegurado: {{ location.insuredValue | number:'1.2-2' }}</p>
        <ul *ngIf="location.alerts.length">
          <li *ngFor="let alert of location.alerts">{{ alert }}</li>
        </ul>
      </div>
    </div>
  `
})
export class LocationsGridComponent {
  @Input() locations: QuoteLocation[] = [];
}
