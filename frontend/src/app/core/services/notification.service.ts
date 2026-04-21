import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificationService {
  readonly messages = signal<string[]>([]);

  push(message: string): void {
    this.messages.update((current) => [...current, message]);
  }

  clear(): void {
    this.messages.set([]);
  }
}
