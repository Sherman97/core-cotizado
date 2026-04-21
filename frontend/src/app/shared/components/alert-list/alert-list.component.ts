import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-alert-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="alert-list" *ngIf="alerts?.length">
      <div class="alert-item" *ngFor="let alert of alerts">{{ alert }}</div>
    </div>
  `
})
export class AlertListComponent {
  @Input() alerts: string[] = [];
}
