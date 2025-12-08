export interface DeletionEligibilityDto {
  canDelete: boolean;
  status: string;
  reason?: string;
}