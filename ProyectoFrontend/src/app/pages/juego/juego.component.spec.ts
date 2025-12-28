import { ComponentFixture, TestBed } from '@angular/core/testing';

import { JuegoComponent } from './juego.component';

describe('JuegoComponent', () => {
  let component: JuegoComponent;
  let fixture: ComponentFixture<JuegoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JuegoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(JuegoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
