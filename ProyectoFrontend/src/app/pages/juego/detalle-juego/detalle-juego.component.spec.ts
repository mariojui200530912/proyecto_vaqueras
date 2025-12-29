import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleJuegoComponent } from './detalle-juego.component';

describe('DetalleJuegoComponent', () => {
  let component: DetalleJuegoComponent;
  let fixture: ComponentFixture<DetalleJuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleJuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalleJuegoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
